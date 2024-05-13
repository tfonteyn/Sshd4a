#include <ctype.h>
#include <fcntl.h>
#include <jni.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <errno.h>
#include <syslog.h>

#include "openssh/version.h"
#include "rsync/version.h"

#include "jni-dropbear.h"
#include "dbrandom.h"
#include "session.h"

/* executable for a shell */
const char *sshd4a_shell_exe = "";
/* the home-directory */
const char *sshd4a_home_path = "";

/* executable lib location. */
const char *lib_path = "";
/* location of configuration files */
const char *conf_path = "";
/* name/value pairs with environment variables */
const char *env_var_list = "";

int enable_public_key_auth = JNI_TRUE;
int enable_single_use_passwords = JNI_TRUE;

/* enable the "buffersu" helper when the user has SuperSu installed. */
int enable_super_su_buffering = JNI_FALSE;

/* Construct the full path to the given configuration file. */
char *sshd4a_conf_file(const char *fn) {
    char *ret = m_malloc(strlen(conf_path) + strlen(fn) + 1 + /* '\0' */ 1);
    sprintf(ret, "%s/%s", conf_path, fn);
    return ret;
}

/* Convert the name of the given executable to the full path with the fake libEXE.so name. */
char *sshd4a_exe_to_lib(const char *cmd) {
    if (cmd && !strncmp(cmd, "scp ", 4)) {
        char *t = m_malloc(strlen(lib_path) + 11 + strlen(cmd) + /* '\0' */ 1);
        sprintf(t, "%s/libscp.so %s", lib_path, cmd + 4);
        return t;

    } else if (cmd && !strncmp(cmd, "rsync ", 6)) {
        char *t;
        if (enable_super_su_buffering) {
            t = m_malloc(strlen(lib_path) + 16 + strlen(cmd) - 6 + /* '\0' */ 1);
            sprintf(t, "%s/libbuffersu.so %s", lib_path, cmd + 6);
        } else {
            t = m_malloc(strlen(lib_path) + 13 + strlen(cmd) - 6 + /* '\0' */ 1);
            sprintf(t, "%s/librsync.so %s", lib_path, cmd + 6);
        }
        return t;

    } else if (cmd && !strncmp(cmd, "sftp-server", 11)) {
        char *t = m_malloc(strlen(lib_path) + 18 + /* '\0' */ 1);
        sprintf(t, "%s/libsftp-server.so", lib_path);
        return t;
    }

    return (char *) cmd;
}

/* Add user specified name/value pairs to the environment. */
void sshd4a_set_env() {
    const char *s = env_var_list;
    if (!s) {
        return;
    }
    while (*s) {
        const char *name, *val;
        long name_len, val_len;

        name = s;
        while (*s && (*s != '=') && (*s != '\r') && (*s != '\n')) {
            s++;
        }
        name_len = s - name;
        if (*s == '=') {
            s++;
            val = s;
            while (*s && (*s != '\r') && (*s != '\n')) {
                s++;
            }
            val_len = s - val;
        } else {
            val = "";
            val_len = 0;
        }
        while ((*s == '\r') || (*s == '\n')) {
            s++;
        }

        if (name_len) {
            char *n = m_malloc(name_len + 1);
            char *v = m_malloc(val_len + 1);
            memcpy(n, name, name_len);
            memcpy(v, val, val_len);
            n[name_len] = 0;
            v[val_len] = 0;
            setenv(n, v, /*overwrite=*/1);    /* might fail *shrug* */
            m_free(n);
            m_free(v);
        }
    }

    /* make available to buffersu. */
    setenv(SSHD4A_LIB_DIR, lib_path, /*overwrite=*/ 1);
    /* make available to rsync */
    setenv(SSHD4A_CONF_DIR, conf_path, /*overwrite=*/ 1);
}

int sshd4a_enable_public_key_login() {
    return enable_public_key_auth;
}

/*
 * returns 1 if "authorized_keys" exists and is longer than
 * MIN_AUTHKEYS_LINE (10 bytes) as defined in "dropbear/svr-authpubkey.c"
 */
__attribute__((unused)) int sshd4a_authorized_keys_exists() {
    char *fn = sshd4a_conf_file("authorized_keys");
    FILE *f = fopen(fn, "r");
    m_free(fn); /* match "m_malloc()" from sshd4a_conf_file */
    if (!f) {
        return 0;
    }
    /* 10 == MIN_AUTHKEYS_LINE */
    for (int i = 0; i < 10; i++) {
        if (fgetc(f) == EOF) {
            fclose(f);
            return 0;
        }
    }
    fclose(f);
    return 1;
}

int sshd4a_enable_single_use_password() {
    return enable_single_use_passwords;
}

void sshd4a_generate_single_use_password(char **gen_pass) {
    /* Don't use Il1O0 because they're visually ambiguous */
    static const char tab64[64] =
            "abcdefghijk!mnopqrstuvwxyzABCDEFGH@JKLMN#PQRSTUVWXYZ$%23456789^&";
    char pw[9];
    int i;
    genrandom((unsigned char *) pw, 8);
    for (i = 0; i < 8; i++) {
        pw[i] = tab64[pw[i] & 63];
    }
    pw[8] = 0;
    dropbear_log(LOG_WARNING, "Single-use password:");
    dropbear_log(LOG_ALERT, "--------");
    dropbear_log(LOG_ALERT, "%s", pw);
    dropbear_log(LOG_ALERT, "--------");

    *gen_pass = m_strdup(pw);
}

int sshd4a_enable_master_password() {
    /* not implemented; the user should just remove the master-user/pass settings in the UI. */
    return 1;
}

int sshd4a_user_password(char **user, char **password) {
    char *fn = sshd4a_conf_file("master_password");
    FILE *f = fopen(fn, "r");
    m_free(fn); /* match "m_malloc()" from sshd4a_conf_file */
    if (!f) {
        return 0;
    }

    int ret_value = 0;

    char *line = NULL;
    size_t len = 0;
    ssize_t read;

    read = getline(&line, &len, f);
    if (read > 0 && strstr(line, ":") != NULL) {
        char *p;
        p = strtok(line, ":");
        *user = strdup(p);

        p = strtok(NULL, ":");
        *password = strdup(p);

        ret_value = 1;
    }
    /* match realloc() from getline(..) */
    free(line);
    fclose(f);
    return ret_value;
}

const char *from_java_string(JNIEnv *env, jstring str) {
    if (!str) {
        return "";
    }
    const char *tmp = (*env)->GetStringUTFChars(env, str, NULL);
    if (!tmp) {
        return "";
    }
    const char *value = strdup(tmp);
    (*env)->ReleaseStringUTFChars(env, str, tmp);
    return value;
}

/*
 * This makes sure that no previously-added atexit gets called (some users have
 * an atexit registered by libGLESv2_adreno.so)
 */
static void null_atexit(void) {
    _Exit(0);
}

/**
 * Main entry point; start dropbear in-process.
 *
 * @param env
 * @param cl
 * @param j_lib_path                 native library directory
 * @param j_dropbear_args            arguments to pass to the dropbear main
 * @param j_conf_path                location for "authorized_keys" etc
 * @param j_home_path                home directory for an ssh login
 * @param j_shell_exe                shell executable
 * @param j_env_var_list             list of environment variables
 * @param j_enablePublickeyAuth      enable public key logins
 * @param j_enableSingleUsePasswords enable generating single-use passwords
 * @param j_enableSuperSuBuffering   enable support for "SuperSu" rsync buffering
 *
 * @return On success, the PID of the dropbear process.  On failure, -1.
 */
JNIEXPORT jint JNICALL
Java_com_hardbacknutter_sshd_SshdService_start_1sshd(
        JNIEnv *env,
        jclass cl,
        jstring j_lib_path,
        jobjectArray j_dropbear_args,
        jstring j_conf_path,
        jstring j_home_path,
        jstring j_shell_exe,
        jstring j_env_var_list,
        jboolean j_enablePublickeyAuth,
        jboolean j_enableSingleUsePasswords,
        jboolean j_enableSuperSuBuffering) {

    pid_t pid = fork();
    if (pid == 0) {
        atexit(null_atexit);

        lib_path = from_java_string(env, j_lib_path);
        conf_path = from_java_string(env, j_conf_path);
        sshd4a_home_path = from_java_string(env, j_home_path);

        sshd4a_shell_exe = from_java_string(env, j_shell_exe);
        env_var_list = from_java_string(env, j_env_var_list);

        enable_public_key_auth = j_enablePublickeyAuth;
        enable_single_use_passwords = j_enableSingleUsePasswords;
        enable_super_su_buffering = j_enableSuperSuBuffering;

        const jsize argc = (*env)->GetArrayLength(env, j_dropbear_args);
        const char *argv[argc];
        for (jint i = 0; i < argc; i++) {
            const jstring j_value = (jstring)
                    ((*env)->GetObjectArrayElement(env, j_dropbear_args, i));
            argv[i] = from_java_string(env, j_value);
        }

        const char *log_fn = sshd4a_conf_file("dropbear.err");
        const char *log_fn_old = sshd4a_conf_file("dropbear.err.old");
        unlink(log_fn_old);
        rename(log_fn, log_fn_old);
        unlink(log_fn);

        const int log_fd = open(log_fn, O_CREAT | O_WRONLY, 0666);
        if (log_fd != -1) {
            /* replace stderr with our logfile */
            dup2(log_fd, 2);
        }
        for (int i = 3; i < 255; i++) {
            /* make sure only stdin/stdout/stderr are left open. */
            close(i);
        }

        // Force the dropbear.err file into existence...
        // The monitoring java thread assumes the file always exists!
        fprintf(stderr, "Starting dropbear\n");

        dropbear_main(argc, (char **) argv, NULL);
        /* not reachable */
        exit(0);

    } else if (pid == -1) {
        fprintf(stderr, "Failed to start dropbear errno=%u\n", errno);
    }

    return pid;
}

JNIEXPORT void JNICALL
Java_com_hardbacknutter_sshd_SshdService_kill(
        JNIEnv *env,
        jclass cl,
        jint pid) {
    kill(pid, SIGKILL);
}

JNIEXPORT int JNICALL
Java_com_hardbacknutter_sshd_SshdService_waitpid(
        JNIEnv *env,
        jclass cl,
        jint pid) {
    int status;
    waitpid(pid, &status, 0);
    if (WIFEXITED(status)) {
        return WEXITSTATUS(status);
    }
    return 0;
}

JNIEXPORT void JNICALL
Java_com_hardbacknutter_sshd_SshdSettings_enableSingleUsePassword(
        JNIEnv *env,
        jobject thiz,
        jboolean j_enable) {
    enable_single_use_passwords = j_enable;
}

JNIEXPORT void JNICALL
Java_com_hardbacknutter_sshd_SshdSettings_enablePublicKeyAuth(
        JNIEnv *env,
        jobject thiz,
        jboolean j_enable) {
    enable_public_key_auth = j_enable;
}

extern const char *rsync_version(void);
JNIEXPORT jstring JNICALL
Java_com_hardbacknutter_sshd_SshdSettings_getRsyncVersion(JNIEnv *env,
                                                          jclass clazz) {
    return (*env)->NewStringUTF(env, RSYNC_VERSION);
}
JNIEXPORT jstring JNICALL
Java_com_hardbacknutter_sshd_SshdSettings_getOpensshVersion(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, SSH_RELEASE);
}

JNIEXPORT jstring JNICALL
Java_com_hardbacknutter_sshd_SshdSettings_getDropbearVersion(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, DROPBEAR_VERSION);
}