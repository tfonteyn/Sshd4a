package com.hardbacknutter.sshd;

public class Prefs {

    public static final String RUN_ON_BOOT = "service.start.onboot";
    public static final String RUN_ON_OPEN = "service.start.onopen";
    public static final String RUN_IN_FOREGROUND = "service.start.foreground";

    public static final String SSHD_PORT = "sshd.port";
    public static final String DEFAULT_PORT = "2222";


    public static final String HOME = "sshd.home";
    public static final String ENV_VARS = "sshd.env";
    public static final String SHELL = "sshd.shell";
    public static final String DEFAULT_SHELL = "/system/bin/sh";

    public static final String DROPBEAR_CMDLINE_OPTIONS = "dropbear.options";

    /**
     * <a href="https://supersuroot.org/">https://supersuroot.org/</a>
     */
    public static final String USE_SUPER_SU_BUFFERING = "rsyncbuffer";
    public static final String UI_THEME = "ui.theme";
    public static final String UI_NOTIFICATION = "ui.notification.permission.ask";
}
