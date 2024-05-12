#ifndef SSHD4A_JNI_DROPBEAR_H
#define SSHD4A_JNI_DROPBEAR_H

/*
 * SSHD4A_REQUIRED_CHANGE
 * Used in comments to indicate code changes which are unconditional.
 */

/*
 * Enable the code to enable/disable:
 * - public key logins
 * - single-use passwords
 * - master-user/password
 */
#define SSHD4A_EXTEND_AUTHENTICATION 1
/*
 * Enable the code that allows mismatches between key and signatures in some bad clients.
 */
#define SSHD4A_ALLOW_RSA_KEY_SIGNATURE_MISMATCH 1

#define SSHD4A_LIB_DIR  "SSHD4A_LIB_DIR"
#define SSHD4A_CONF_DIR "SSHD4A_CONF_DIR"

extern const char *sshd4a_shell_exe;
extern const char *sshd4a_home_path;

char *sshd4a_conf_file(const char *fn);

char *sshd4a_exe_to_lib(const char *cmd);

void sshd4a_set_env();

int sshd4a_enable_public_key_login();

int sshd4a_enable_single_use_password();

void sshd4a_generate_single_use_password(char **gen_pass);

int sshd4a_enable_master_password();

int sshd4a_user_password(char **user, char **password);

#endif /* SSHD4A_JNI_DROPBEAR_H */
