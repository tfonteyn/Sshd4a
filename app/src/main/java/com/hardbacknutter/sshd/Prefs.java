package com.hardbacknutter.sshd;

public final class Prefs {

    static final String RUN_ON_BOOT = "service.start.onboot";
    static final String RUN_ON_OPEN = "service.start.onopen";
    static final String RUN_IN_FOREGROUND = "service.start.foreground";

    static final String SSHD_PORT = "sshd.port";
    static final String DEFAULT_PORT = "2222";

    static final String HOME = "sshd.home";
    static final String ENV_VARS = "sshd.env";
    static final String SHELL = "sshd.shell";
    static final String DEFAULT_SHELL = "/system/bin/sh";

    static final String DROPBEAR_CMDLINE_OPTIONS = "dropbear.options";

    /**
     * <a href="https://supersuroot.org/">https://supersuroot.org/</a>
     */
    static final String USE_SUPER_SU_BUFFERING = "rsyncbuffer";
    static final String UI_THEME = "ui.theme";
    static final String UI_NOTIFICATION_ASK_PERMISSION = "ui.notification.ask_permission";

    private Prefs() {
    }
}
