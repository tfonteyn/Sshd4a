package com.hardbacknutter.sshd;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainViewModel
        extends ViewModel {

    /** Filename used in native code. Stored in {@link SshdService#getDropbearDirectory}. */
    private static final String AUTHORIZED_KEYS = "authorized_keys";
    /** Filename used in native code. Stored in {@link SshdService#getDropbearDirectory}. */
    private static final String MASTER_PASSWORD = "master_password";

    private static final int THREAD_SLEEP_MILLIS = 2000;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final String TAG = "MainViewModel";
    private final AtomicBoolean cancelRequested = new AtomicBoolean();
    private final MutableLiveData<String> logData = new MutableLiveData<>();
    private final MutableLiveData<Pair<String, Integer>> startStopButton = new MutableLiveData<>();
    private SharedPreferences pref;

    /** text and colorInt */
    private Pair<String, Integer> startBtn;
    private Pair<String, Integer> stopBtn;

    @NonNull
    MutableLiveData<String> onLogUpdate() {
        return logData;
    }

    MutableLiveData<Pair<String, Integer>> onUpdateUI() {
        return startStopButton;
    }

    void init(@NonNull final Context context) {
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);

            //noinspection resource
            final TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.startButtonColor,
                                                                          R.attr.stopButtonColor});
            int startColor;
            int stopColor;
            try {
                startColor = a.getColor(0, 0);
                stopColor = a.getColor(1, 0);
                if (startColor == 0 || stopColor == 0) {
                    throw new Resources.NotFoundException();
                }
            } finally {
                a.recycle();
            }

            startBtn = new Pair<>(context.getString(R.string.btn_start), startColor);
            stopBtn = new Pair<>(context.getString(R.string.btn_stop), stopColor);
        }
    }

    void updateUI() {
        startStopButton.setValue(SshdService.isRunning() ? stopBtn : startBtn);
    }

    boolean isRunOnOpen() {
        return pref.getBoolean(Prefs.RUN_ON_OPEN, false);
    }

    boolean startService(@NonNull final Context context) {
        cancelUpdateThread();
        ComponentName componentName = null;
        try {
            componentName = SshdService
                    .startService(SshdService.Started.ByUser, context);

            startUpdateThread(context);

        } catch (@NonNull final Exception e) {
            // On devices with API 31, theoretically we could see these:
            // ForegroundServiceStartNotAllowedException
            // BackgroundServiceStartNotAllowedException
            // ... but we shouldn't... flw
            Log.e(TAG, "", e);
        }

        return componentName != null;
    }

    void stopService(@NonNull final Context context) {
        cancelUpdateThread();
        SshdService.stopService(context);
    }

    @AnyThread
    void cancelUpdateThread() {
        cancelRequested.set(true);
    }

    void startUpdateThread(@NonNull final Context context) {
        cancelRequested.set(false);
        final String path = SshdService.getDropbearDirectory(context).getPath();
        // poll for changes to the dropbear error file
        EXECUTOR_SERVICE.execute(() -> {
            final File file = new File(path, SshdService.DROPBEAR_ERR);
            long lastModified = 0;
            long lastLength = 0;
            while (!cancelRequested.get()) {
                final long mod = file.lastModified();
                final long len = file.length();
                if ((mod != lastModified) || (len != lastLength)) {
                    logData.postValue(collectLogLines(file));

                    lastModified = mod;
                    lastLength = len;
                }
                try {
                    //noinspection BusyWait
                    Thread.sleep(THREAD_SLEEP_MILLIS);
                } catch (@NonNull final InterruptedException e) {
                    break;
                }
            }
        });
    }

    @WorkerThread
    @NonNull
    private String collectLogLines(@NonNull final File file) {
        final List<String> all = new ArrayList<>();
        try {
            if (file.exists()) {
                try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        all.add(line);
                    }
                }
            }
        } catch (@NonNull final Exception ignore) {
            // ignore
        }

        final int size = all.size();
        if (size > BuildConfig.NR_OF_LOG_LINES) {
            return String.join("\n", all.subList(size - BuildConfig.NR_OF_LOG_LINES, size));
        } else {
            return String.join("\n", all);
        }
    }

    /**
     * Import the given uri as the new "authorized_keys" file; overwrites the previous!
     *
     * @param context Current context
     * @param uri     to import
     *
     * @return {@code null} on success; an error message on failure
     */
    @Nullable
    String importAuthKeys(@NonNull final Context context,
                          @NonNull final Uri uri) {
        final File path = SshdService.getDropbearDirectory(context);

        final File tmpFile = new File(path, AUTHORIZED_KEYS + ".tmp");

        String error = null;

        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!tmpFile.renameTo(new File(path, AUTHORIZED_KEYS))) {
                error = context.getString(R.string.err_key_import);
            }
        } catch (@NonNull final IOException e) {
            error = context.getString(R.string.err_key_import) + '\n' + e.getLocalizedMessage();
        } finally {
            //noinspection ResultOfMethodCallIgnored
            tmpFile.delete();
        }
        return error;
    }

    void deleteAuthKeys(@NonNull final Context context) {
        //noinspection ResultOfMethodCallIgnored
        new File(SshdService.getDropbearDirectory(context), AUTHORIZED_KEYS).delete();
    }

    @Nullable
    String readMasterPassword(@NonNull final Context context) {
        final File path = SshdService.getDropbearDirectory(context);
        final File file = new File(path, MASTER_PASSWORD);
        final List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                return lines.get(0);
            }
        } catch (@NonNull final IOException ignore) {
            // ignore
        }
        return null;
    }

    void writeMasterPassword(@NonNull final Context context,
                             @Nullable final String password)
            throws IOException {
        final File path = SshdService.getDropbearDirectory(context);
        final File file = new File(path, MASTER_PASSWORD);
        if (password == null || password.isBlank()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } else {
            //noinspection ImplicitDefaultCharsetUsage
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(password.toCharArray());
            }
        }
    }

    boolean isAskNotificationPermission() {
        return pref.getBoolean(Prefs.UI_NOTIFICATION_ASK_PERMISSION, true);
    }

    void setAskNotificationPermission(
            @SuppressWarnings("SameParameterValue") final boolean shouldAsk) {
        pref.edit().putBoolean(Prefs.UI_NOTIFICATION_ASK_PERMISSION, shouldAsk).apply();
    }
}
