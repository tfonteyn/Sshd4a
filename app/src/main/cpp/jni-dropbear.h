/*
 * This file should be included in "dropbear/localoptions.h" ONLY.
 */
#ifndef SSHD4A_JNI_DROPBEAR_H
#define SSHD4A_JNI_DROPBEAR_H

/* enable the code to create single-use passwords if there is no "authorized_keys" file. */
#define ANDROID_SSHD_SINGLE_USE_PASSWORD 1
/* enable the code that allows mismatches between key and signatures in some bad clients. */
#define ANDROID_SSHD_ALLOW_RSA_KEY_SIGNATURE_MISMATCH 1

extern const char *sshd4a_shell_exe;
extern const char *sshd4a_home_path;

char *sshd4a_conf_file(const char *fn);
char *sshd4a_exe_to_lib(const char *cmd);
void sshd4a_set_env();

void sshd4a_hook__svr_auth__svr_authinitialise();

int  sshd4a_enable_public_key_login();

__attribute__((unused)) int  sshd4a_authorized_keys_exists();
int  sshd4a_enable_single_use_password();
void sshd4a_generate_single_use_password(char **gen_pass);
int  sshd4a_enable_master_password();
int  sshd4a_user_password(char **user, char **password);

#endif //SSHD4A_JNI_DROPBEAR_H
