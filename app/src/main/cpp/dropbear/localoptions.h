/*
 * See ../jni-dropbear.c:
 */
extern const char *sshd4a_shell_exe;
extern const char *sshd4a_home_path;

char *sshd4a_conf_file(const char *fn);
char *sshd4a_exe_to_lib(const char *cmd);
void sshd4a_set_env();
int  sshd4a_authorized_keys_exists();

/* enable the code to create single-use passwords if there is no "authorized_keys" file. */
#define ANDROID_SSHD_SINGLE_USE_PASSWORD 1
/* enable the code that allows mismatches between key and signatures in some bad clients. */
#define ANDROID_SSHD_ALLOW_RSA_KEY_SIGNATURE_MISMATCH 1

/*
 * The below are all dropbear specific settings.
 * See default_options.h which documents compile-time options, and provides default values.
*/

#define DROPBEAR_DEFPORT "2222"

/* Add trace code - to make this work, SshService.java#startSshd
 * must also pass in "-v" (up to "-vvvvv") to dropbearArgs. */
/* #define DEBUG_TRACE 5 */


#define DSS_PRIV_FILENAME     sshd4a_conf_file("dropbear_dss_host_key")
#define RSA_PRIV_FILENAME     sshd4a_conf_file("dropbear_rsa_host_key")
#define ECDSA_PRIV_FILENAME   sshd4a_conf_file("dropbear_ecdsa_host_key")
#define ED25519_PRIV_FILENAME sshd4a_conf_file("dropbear_ed25519_host_key")

#define DROPBEAR_PIDFILE      sshd4a_conf_file("dropbear.pid")

#define DROPBEAR_SFTPSERVER 1
#define SFTPSERVER_PATH       sshd4a_exe_to_lib("sftp-server")


#define DROPBEAR_SERVER 1
#define DBMULTI_dropbear 1
#define DROPBEAR_MULTI 1

#define DROPBEAR_SMALL_CODE 0
#define INETD_MODE 0
#define DROPBEAR_CLI_LOCALTCPFWD 0
#define DROPBEAR_CLI_REMOTETCPFWD 0
#define DROPBEAR_CLI_AGENTFWD 0
#define DROPBEAR_CLI_PROXYCMD 0
#define DROPBEAR_CLI_NETCAT 0
#define DROPBEAR_CLI_PASSWORD_AUTH 0
#define DROPBEAR_CLI_PUBKEY_AUTH 0

#define DROPBEAR_SVR_AGENTFWD 0
#define DROPBEAR_SVR_PASSWORD_AUTH 0

/* not literally true, but we can only support a single user */
#define DROPBEAR_SVR_MULTIUSER 0

#define DROPBEAR_USER_ALGO_LIST 0
#define DROPBEAR_ENABLE_GCM_MODE 1
#define DROPBEAR_SHA2_512_HMAC 1

#define DO_MOTD 0
#define XAUTH_COMMAND 0

#define DROPBEAR_PATH_SSH_PROGRAM "/dev/null"

#define DEFAULT_RECV_WINDOW (512*1024)
#define RECV_MAX_PAYLOAD_LEN (128*1024)

#define DEFAULT_PATH "/sbin:/system/sbin:/system/bin:/system/xbin"



