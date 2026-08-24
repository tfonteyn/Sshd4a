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
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.hardbacknutter.sshd.settings.Prefs;

/**
 * <a href="https://developer.android.com/guide/components/services.html#Lifecycle">
 * https://developer.android.com/guide/components/services.html#Lifecycle</a>
 * <p>
 * <a href="https://developer.android.com/about/versions/oreo/background#services">
 * Background Service Limitations</a>
 * <pre>
 *     While an app is in the foreground, it can create and run both foreground
 *     and background services freely. When an app goes into the background,
 *     it has a window of several minutes in which it is still allowed to create
 *     and use services. At the end of that window, the app is considered to be idle.
 *     At this time, the system stops the app's background services
 * </pre>
 * i.o.w. if the user clicks the "start" button or {@link Prefs#isRunOnAppStart}) is used,
 * the service MUST use "foreground".
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

    private static final Object lock = new Object();
    /**
     * Singleton.
     */
    @Nullable
    @GuardedBy("lock")
    private static SshdService sInstance = null;
    @Nullable
    private static StartMode startMode = null;

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
    private SharedPreferences prefs;
    private boolean notificationChannelCreated;

    /**
     * What is the current mode.
     * <p>
     * Only valid when we're actually running.
     *
     * @return mode
     */
    @Nullable
    static StartMode getStartMode() {
        return startMode;
    }

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
     * @param context   Current context
     * @param startMode identifier for the caller
     *
     * @return the ComponentName, or {@code null} if it failed to start but did not throw.
     *
     * @throws SecurityException     if the usr has no permission
     * @throws IllegalStateException API30: starting failed.
     *                               API31+ will instead throw one of:
     *                               {@link ForegroundServiceStartNotAllowedException}
     *                               {@link BackgroundServiceStartNotAllowedException}
     * @see #stopService(Context)
     */
    @Nullable
    static ComponentName startService(@NonNull final Context context,
                                      @NonNull final StartMode startMode)
            throws IllegalStateException {
        SshdService.startMode = startMode;

        switch (startMode) {
            case ByUser: {
                final Intent intent = new Intent(context, SshdService.class);
                if (Prefs.isRunInForeground(context)) {
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

    /**
     * Stop the service.
     *
     * @param context Current context
     *
     * @throws SecurityException     If the caller does not have permission to access the service
     *                               or the service can not be found.
     * @throws IllegalStateException If the application is in a state where the service
     *                               can not be started (such as not in the foreground in a state
     *                               when services are allowed).
     */
    static void stopService(@NonNull final Context context) {
        final Intent intent = new Intent(context, SshdService.class);
        context.stopService(intent);
        startMode = null;
    }

    public static native String getDropbearVersion();

    public static native String getOpensshVersion();

    public static native String getRsyncVersion();

    private native int start_sshd(@NonNull String lib,
                                  @NonNull String[] dropbearArgs,
                                  @NonNull String confPath,
                                  @NonNull String homePath,
                                  @NonNull String shell,
                                  @NonNull String env,
                                  boolean enableServiceShellAccess,
                                  boolean enablePublickeyAuth,
                                  boolean enableSingleUsePasswords);

    private native void kill(int pid);

    private native int waitpid(int pid);

    @SuppressWarnings({"ImplicitDefaultCharsetUsage", "BlockingMethodInNonBlockingContext"})
    private int readPidFile() {
        final File pidFile = new File(SshdSettings.getDropbearDirectory(this), DROPBEAR_PID);
        int pid = 0;
        if (pidFile.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(pidFile))) {
                pid = Integer.parseInt(r.readLine());
            } catch (@NonNull final NumberFormatException | IOException ignore) {
                // ignore
            }
        }
        return pid;
    }

    /**
     * Gather arguments, start the native code and set up a watchdog.
     */
    private void startSshd() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + "|startSshd", "ENTER");
        }

        // kill any stale ssh process lingering around
        stopSshd();

        // See all options: cpp/dropbear/src/svr-runopts.c
        final List<String> argList = new ArrayList<>();
        // the command to run; i.e. args[0]
        argList.add("sshd");
        // Pass on the android system environment to the child process which allows
        // utilities like am (activity manager) and pm (package manager) to run,
        argList.add("-e");
        // Create host keys as required
        argList.add("-R");
        // Don't fork into background
        argList.add("-F");

        // edit dropbear/config.h, add:  #define DEBUG_TRACE 1
        // before enabling the next line
//        args.add("-v");

        final List<String> userOptions = Prefs.getCmdLineOptions(prefs);
        // If the user has not set a manual bind
        if (!userOptions.contains("-p")) {
            // then bind to [address:]port, which defaults to all interfaces
            argList.add("-p");
            argList.add(String.valueOf(Prefs.getPort(prefs)));
        }

        argList.addAll(userOptions);

        final String[] args = argList.toArray(Z_STRING);
        final String confPath = SshdSettings.getDropbearDirectory(this).getPath();
        final String homePath = Prefs.getHomePath(this, prefs);
        final String shellCmd = Prefs.getShellCmd(prefs);
        final String env = Prefs.getEnv(prefs);

        final boolean enableServiceShellAccess = Prefs.isEnableServiceShellAccess(prefs);
        final boolean enablePublickeyLogin = Prefs.isEnablePublicKeyAuth(prefs);
        final boolean enableSingleUsePasswords = Prefs.isEnableSingleUsePasswordAuth(prefs);

        final int pid = start_sshd(getApplicationInfo().nativeLibraryDir,
                                   args,
                                   confPath, homePath, shellCmd, env,
                                   enableServiceShellAccess,
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

    /**
     * Stop/kill the native code.
     */
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
        prefs = Prefs.getSharedPreferences(this);

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

        // STORE the value, we need it in onDestroy
        // and the user might change it in prefs while we're running
        runInForeground = Prefs.isRunInForeground(prefs);

        startSshd();

        if (runInForeground) {
            final String text;

            final List<String> bindings = Prefs.getBindings(prefs);
            if (bindings.isEmpty()) {
                // We should only get here if the user options are invalid.
                // We're assuming (rightly or wrongly) that the user knows
                // how to use correct "-p" options
                text = getString(R.string.err_no_ip);
            } else {
                final String s;
                if (bindings.size() > 1) {
                    s = bindings.stream().collect(Collectors.joining(",", "[", "]"));
                } else {
                    s = bindings.get(0);
                }

                text = getString(R.string.notification_listeners_list, s);
            }
            final Notification notification = createNotification(text);

            // GitHub #34: with targetSdk 37, on a physical Android 17 (Pixel 7a)
            // startup failed using the 2-arg call.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(ONGOING_NOTIFICATION_ID, notification,
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                );
            } else {
                startForeground(ONGOING_NOTIFICATION_ID, notification);
            }
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
            stopForeground(STOP_FOREGROUND_REMOVE);
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
        if (!notificationChannelCreated) {
            final NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationChannel nc = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(nc);
            notificationChannelCreated = true;
        }

        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0,
                                          new Intent(this, MainActivity.class),
                                          PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.app)
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
        //noinspection deprecation
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
