package com.hardbacknutter.sshd;

import android.app.BackgroundServiceStartNotAllowedException;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://developer.android.com/guide/components/services.html#Lifecycle">
 * https://developer.android.com/guide/components/services.html#Lifecycle</a>
 * <p>
 * <p>
 * Windows OS port forwarding from the external listenaddress to the localhost:
 * Use "ipconfig" on the windows box to get your current address.
 * <pre>
 *     netsh interface portproxy add v4tov4 listenaddress=<host-ip> listenport=2222 connectaddress=127.0.0.1 connectport=2222
 * </pre>
 * <p>
 * Open the firewall:
 * <pre>
 *     netsh advfirewall firewall add rule name="ALLOW TCP PORT 2222" dir=in action=allow protocol=TCP localport=2222
 * </pre>
 * <p>
 * To access the emulator from a shell on the emulator hosting machine, run:
 * <pre>
 *    # optional / might be needed!
 *    adb root
 *    # enable the forward
 *    adb forward tcp:2222 tcp:2222
 *    # you can now connect to the sshd server on the emulator with:
 *    ssh -p 2222 localhost
 * </pre>
 * <p>
 * Close the firewall:
 * <pre>
 *     netsh advfirewall firewall delete rule name="ALLOW TCP PORT 2222"
 * </pre>
 *
 * <a href="https://developer.android.com/about/versions/oreo/background#services">
 * Background Service Limitations</a>
 * <pre>
 *     While an app is in the foreground, it can create and run both foreground
 *     and background services freely. When an app goes into the background,
 *     it has a window of several minutes in which it is still allowed to create
 *     and use services. At the end of that window, the app is considered to be idle.
 *     At this time, the system stops the app's background services
 * </pre>
 * ==> A manual start (i.e. user clicks the button or 'startOnOpn') should always use "foreground".
 */
public class SshdService
        extends Service {

    /**
     * Intent filter name for receiving broadcasts.
     * This service will send the broadcast; the UI should listen for it.
     */
    static final String SERVICE_UI_REQUEST = "ServiceUIRequest";

    private static final String NOTIFICATION_CHANNEL_ID =
            "com.hardbacknutter.sshd.NOTIFICATION_CHANNEL";
    private static final int ONGOING_NOTIFICATION_ID = 1;

    private static final String DROPBEAR_PID = "dropbear.pid";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    /* If restarting sshd twice within 10 seconds, give up. */
    private static final int MIN_DURATION_MS = 10_000;

    /** Log tag. */
    private static final String TAG = "SshdService";
    private static final String[] Z_STRING = new String[0];

    /**
     * Extra command line options to pass to the dropbear executable.
     * Splits on spaces, but respects " and \
     */
    private static final Pattern CMD_OPTIONS_PATTERN = Pattern.compile(
            "\\S*\"([^\"]*)\"\\S*|(\\S+)");

    private static final Object lock = new Object();
    /**
     * Singleton.
     */
    @Nullable
    @GuardedBy("lock")
    private static SshdService sInstance;

    static {
        System.loadLibrary("jni-dropbear");
    }

    @GuardedBy("lock")
    private int sshdPid;
    @GuardedBy("lock")
    private long sshdStartTime;
    @GuardedBy("lock")
    private long sshdDuration;

    /**
     * Running in foreground is presumed to be always true UNLESS deliberately switched off.
     */
    private boolean runInForeground = true;
    private SharedPreferences pref;
    private boolean channelCreated;
    private String bindAddress;

    /**
     * Check if the native process is running.
     *
     * @return flag
     */
    static boolean isRunning() {
        synchronized (lock) {
            if (sInstance == null) {
                return false;
            }
            return sInstance.sshdPid > 0;
        }
    }

    /**
     * Start the service.
     *
     * @param startMode identifier for the caller
     * @param context   Current context
     *
     * @return the ComponentName, or {@code null} if it failed to start but did not throw.
     *
     * @throws IllegalStateException if starting failed.
     *                               On API31+ this would actually be one of:
     *                               {@link ForegroundServiceStartNotAllowedException}
     *                               {@link BackgroundServiceStartNotAllowedException}
     */
    @Nullable
    static ComponentName startService(@NonNull final StartMode startMode,
                                      @NonNull final Context context)
            throws IllegalStateException {

        switch (startMode) {
            case ByUser: {
                final Intent intent = new Intent(context, SshdService.class);
                if (Prefs.isUserForeground(context)) {
                    return context.getApplicationContext().startForegroundService(intent);
                } else {
                    return context.getApplicationContext().startService(intent);
                }
            }
            case ByIntent:
            case OnBoot: {
                // Always foreground as required by latest Android version.
                // Will keep running even if the App goes to the background.
                final Intent intent = new Intent(context, SshdService.class);
                return context.getApplicationContext().startForegroundService(intent);
            }
        }
        throw new IllegalStateException("started=" + startMode);
    }

    static boolean stopService(@NonNull final Context context) {
        final Intent intent = new Intent(context, SshdService.class);
        return context.stopService(intent);
    }

    public static native String getDropbearVersion();

    public static native String getOpensshVersion();

    public static native String getRsyncVersion();

    @NonNull
    private String getHomePath(@NonNull final SharedPreferences pref) {
        String homePath = pref.getString(Prefs.HOME, null);
        if (homePath == null || !new File(homePath).exists()) {
            homePath = getFilesDir().getPath();
        }
        return homePath;
    }

    private native int start_sshd(@NonNull String lib,
                                  @NonNull String[] dropbearArgs,
                                  @NonNull String confPath,
                                  @NonNull String homePath,
                                  @NonNull String shell,
                                  @NonNull String env,
                                  boolean enablePublickeyAuth,
                                  boolean enableSingleUsePasswords);

    private native void kill(int pid);

    private native int waitpid(int pid);

    private int readPidFile() {
        final File pidFile = new File(SshdSettings.getDropbearDirectory(this), DROPBEAR_PID);
        int pid = 0;
        if (pidFile.exists()) {
            //noinspection ImplicitDefaultCharsetUsage
            try (BufferedReader r = new BufferedReader(new FileReader(pidFile))) {
                pid = Integer.parseInt(r.readLine());
            } catch (@NonNull final IOException ignore) {
                // ignore
            }
        }
        return pid;
    }

    private void startSshd() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|startSshd", "ENTER");
        }

        // kill any stale ssh process lingering around
        stopSshd();

        // See all options: cpp/dropbear/svr-runopts.c
        final List<String> argList = new ArrayList<>();
        argList.add("sshd");
        // Pass on the android system environment to the child process which allows
        // utilities like am (activity manager) and pm (package manager) to run,
        argList.add("-e");
        // Create host keys as required
        argList.add("-R");
        // Don't fork into background
        argList.add("-F");
        // Bind to [address:]port
        argList.add("-p");
        argList.add(bindAddress);
        // edit dropbear/config.h, add:  #define DEBUG_TRACE 1
        // before enabling the next line
//        args.add("-v");

        final Matcher matcher = CMD_OPTIONS_PATTERN.matcher(
                pref.getString(Prefs.DROPBEAR_CMDLINE_OPTIONS, ""));
        while (matcher.find()) {
            argList.add(matcher.group());
        }
        final String[] args = argList.toArray(Z_STRING);

        final String confPath = SshdSettings.getDropbearDirectory(this).getPath();
        final String homePath = getHomePath(pref);

        String shellCmd = pref.getString(Prefs.SHELL, null);
        if (shellCmd == null || !new File(shellCmd).exists()) {
            shellCmd = Prefs.DEFAULT_SHELL;
        }

        final String env = pref.getString(Prefs.ENV_VARS, "");

        final boolean enablePublickeyLogin =
                pref.getBoolean(Prefs.ENABLE_PUBLIC_KEY_LOGIN, true);
        final boolean enableSingleUsePasswords =
                pref.getBoolean(Prefs.ENABLE_SINGLE_USE_PASSWORDS, true);

        final int pid = start_sshd(getApplicationInfo().nativeLibraryDir,
                                   args, confPath, homePath, shellCmd, env,
                                   enablePublickeyLogin,
                                   enableSingleUsePasswords);
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|startSshd", "start_sshd=" + pid);
        }

        if (pid == -1) {
            // utter failure to start
            synchronized (lock) {
                sshdPid = 0;
            }
        } else {
            synchronized (lock) {
                sshdPid = pid;
            }

            // Start a watchdog thread which will restart sshd should it die.
            EXECUTOR_SERVICE.execute(() -> {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG + "|startSshd", "waitpid=" + pid);
                }
                synchronized (lock) {
                    final long now = System.nanoTime();
                    if (sshdStartTime == 0) {
                        // This is a 'first' start
                        sshdDuration = 0;
                    } else {
                        // It's a restart; remember how long since the last start
                        sshdDuration = now - sshdStartTime;
                    }
                    sshdStartTime = now;
                }

                // Pause until the dropbear process changes state
                waitpid(pid);

                final boolean failed;
                final boolean restart;
                synchronized (lock) {
                    failed = (sshdPid == pid);
                    if (failed) {
                        sshdPid = 0;
                        restart =
                                // 'first' start failed
                                sshdDuration == 0
                                // or the 'previous' attempt ran for more than the minimum
                                || sshdDuration >= MIN_DURATION_MS
                                // or the 'current' attempt ran for more than the minimum
                                || (System.currentTimeMillis() - sshdStartTime)
                                   >= MIN_DURATION_MS;
                        // i.e. do not restart if we failed twice in less than the minimum time.

                    } else {
                        // The process was stopped by request
                        restart = false;
                    }
                }

                if (restart) {
                    startSshd();
                } else if (failed) {
                    // not restarting and failed
                    updateUI();
                }
            });
        }

        updateUI();
    }

    private void stopSshd() {
        synchronized (lock) {
            final int pid = sshdPid;
            sshdPid = 0;
            if (pid > 0) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG + "|stopSshd", "killing pid=" + pid);
                }
                kill(pid);
            }
        }
        updateUI();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|onCreate", "ENTER");
        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        // check for, and kill any potentially stale ssh process
        synchronized (lock) {
            sshdPid = readPidFile();
            if (sshdPid > 0) {
                stopSshd();
            }
        }

        sInstance = this;
    }

    @Override
    @Nullable
    public IBinder onBind(@Nullable final Intent intent) {
        return null;
    }

    /**
     * Start the sshd process.
     *
     * @param intent The Intent supplied to Context.startService, as given.
     *               This may be null if the service is being restarted after
     *               its process has gone away.
     *               ==> Reminder: do NOT use for config data...
     */
    @Override
    public int onStartCommand(@Nullable final Intent intent,
                              final int flags,
                              final int startId) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|onStartCommand", "ENTER");
        }

        runInForeground = pref.getBoolean(Prefs.RUN_IN_FOREGROUND, true);
        bindAddress = pref.getString(Prefs.SSHD_PORT, Prefs.DEFAULT_PORT).strip();

        startSshd();

        if (runInForeground) {
            final List<String> ipList = SshdSettings.getHostAddresses(3);
            if (ipList.isEmpty()) {
                throw new IllegalStateException();
            }

            final String s;
            if (ipList.size() > 1) {
                s = ipList.stream()
                          .collect(Collectors.joining(",", "[", "]"));
            } else {
                s = ipList.get(0);
            }

            startForeground(ONGOING_NOTIFICATION_ID, createNotification(
                    getString(R.string.info_listening_on_ip_port, s, bindAddress)));
        }

        // If we (i.e. this service, which is != this sshd process) get killed,
        // after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|onDestroy", "ENTER");
        }
        stopSshd();
        if (runInForeground) {
            stopForeground(true);
        }

        sInstance = null;

        super.onDestroy();
    }

    /**
     * See {@link Service#startForeground(int, Notification)}
     * Apps targeting API Build.VERSION_CODES.P or later must request the permission
     * "android.Manifest.permission.FOREGROUND_SERVICE" in order to use this API.
     */
    private Notification createNotification(@NonNull final CharSequence text) {
        if (!channelCreated) {
            final NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationChannel nc = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(nc);
            channelCreated = true;
        }

        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0,
                                          new Intent(this, MainActivity.class),
                                          PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app)
                .setTicker(getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLocalOnly(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private void updateUI() {
        final Intent intent = new Intent(SERVICE_UI_REQUEST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public enum StartMode {
        ByUser,
        ByIntent,
        OnBoot
    }
}
