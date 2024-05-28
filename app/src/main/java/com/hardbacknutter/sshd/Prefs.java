package com.hardbacknutter.sshd;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

final class Prefs {

    static final String RUN_ON_BOOT = "service.start.onboot";
    static final String RUN_ON_OPEN = "service.start.onopen";
    static final String RUN_ON_INTENT = "service.start.on.intent.allowed";
    static final String RUN_IN_FOREGROUND = "service.start.foreground";

    static final String SSHD_PORT = "sshd.port";
    static final String DEFAULT_PORT = "2222";

    static final String ENABLE_SINGLE_USE_PASSWORDS = "sshd.enable.single.use.password";
    static final String ENABLE_PUBLIC_KEY_LOGIN = "sshd.enable.publickey.login";

    static final String HOME = "sshd.home";
    static final String ENV_VARS = "sshd.env";
    static final String SHELL = "sshd.shell";
    static final String DEFAULT_SHELL = "/system/bin/sh";

    static final String DROPBEAR_CMDLINE_OPTIONS = "dropbear.options";

    static final String UI_THEME = "ui.theme";
    static final String UI_NOTIFICATION_ASK_PERMISSION = "ui.notification.ask_permission";

    private Prefs() {
    }

    /**
     * Start the service when the device is booted.
     */
    static boolean isStartOnBoot(@NonNull final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                                .getBoolean(RUN_ON_BOOT, false);
    }

    /**
     * Whether an external Intent can start app+service and stop the service+app.
     */
    static boolean isStartByIntentAllowed(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                                .getBoolean(RUN_ON_INTENT, false);
    }

    /**
     * Start the service when the app is started.
     */
    static boolean isRunOnAppStart(@NonNull final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(RUN_ON_OPEN, false);
    }

    /**
     * How the service should be handled by the system when the App goes to the background.
     *
     * @param context Current context
     *
     * @return {@code true}: the service should keep running
     *         {@code false}: the system can kill the service
     */
    static boolean isUserForeground(@NonNull final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(RUN_IN_FOREGROUND, true);
    }

    /**
     * The user configured port to listen on.
     *
     * @param context Current context
     *
     * @return port
     */
    @NonNull
    static String getPort(@NonNull final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(SSHD_PORT, DEFAULT_PORT)
                .strip();
    }

}
