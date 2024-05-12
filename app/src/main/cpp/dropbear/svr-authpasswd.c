/*
 * Dropbear - a SSH2 server
 * 
 * Copyright (c) 2002,2003 Matt Johnston
 * All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. */

/* Validates a user password */

#include "includes.h"
#include "session.h"
#include "buffer.h"
#include "dbutil.h"
#include "auth.h"
#include "runopts.h"

#if defined(SSHD4A_EXTEND_AUTHENTICATION) || defined(DROPBEAR_SVR_PASSWORD_AUTH)

/* not constant time when strings are differing lengths. 
 string content isn't leaked, and crypt hashes are predictable length. */
static int constant_time_strcmp(const char* a, const char* b) {
	size_t la = strlen(a);
	size_t lb = strlen(b);

	if (la != lb) {
		return 1;
	}

	return constant_time_memcmp(a, b, la);
}

/* Process a password auth request, sending success or failure messages as
 * appropriate */
void svr_auth_password(int valid_user) {
	
	char * passwdcrypt = NULL; /* the crypt from /etc/passwd or /etc/shadow */
	char * testcrypt = NULL; /* crypt generated from the user's password sent */
	char * password = NULL;
	unsigned int passwordlen;
	unsigned int changepw;

	/* check if client wants to change password */
	changepw = buf_getbool(ses.payload);
	if (changepw) {
		/* not implemented by this server */
		send_msg_userauth_failure(0, 1);
		return;
	}

	password = buf_getstring(ses.payload, &passwordlen);
	if (valid_user && passwordlen <= DROPBEAR_MAX_PASSWORD_LEN) {
#ifndef SSHD4A_EXTEND_AUTHENTICATION
        /* the first bytes of passwdcrypt are the salt */
		passwdcrypt = ses.authstate.pw_passwd;
		testcrypt = crypt(password, passwdcrypt);
#else /* SSHD4A_EXTEND_AUTHENTICATION */

        char *master_user = NULL;
        char *master_pass = NULL;
        int has_master = sshd4a_user_password(&master_user, &master_pass);
        /* If we have a master user/pass set */
        if (has_master && *master_user && *master_pass
            /* and the user name matches */
            && strcmp(master_user, ses.authstate.username) == 0) {
            /* then we will expect to receive the master password. */
            ses.authstate.pw_passwd = m_strdup(master_pass);
            passwdcrypt = ses.authstate.pw_passwd;

            size_t pas_len = strlen(password);
            if (passwordlen == pas_len) {
                unsigned long hashSize = sha512_desc.hashsize;
                unsigned char *hashResult = (unsigned char*) malloc(hashSize);
                hash_state md;

                sha512_init(&md);
                sha512_process(&md, (const unsigned char*) password, passwordlen);
                sha512_done(&md, hashResult);

                /* 128 is to large for base64, but suits hex should we need it. */
                unsigned long base64_len = 2 * hashSize;
                testcrypt = malloc(base64_len);
                base64_encode(hashResult, hashSize,
                              (unsigned char*) testcrypt, &base64_len);
            } else {
                testcrypt = NULL;
            }
        } else {
            /* Not the master_user, we'll test for a single use password */
            passwdcrypt = ses.authstate.pw_passwd;

            size_t pas_len = strlen(password);
            if (passwordlen == pas_len) {
                char *tmp = malloc(passwordlen + 1);
                strcpy(tmp, password);
                testcrypt = tmp;
            } else {
                testcrypt = NULL;
            }
        }

        /* match malloc's from sshd4a_user_password */
        if (*master_user) {
            free(master_user);
        }
        if (*master_pass) {
            free(master_pass);
        }

#endif /* SSHD4A_EXTEND_AUTHENTICATION */
	}
	m_burn(password, passwordlen);
	m_free(password);

	/* After we have got the payload contents we can exit if the username
	is invalid. Invalid users have already been logged. */
	if (!valid_user) {
		send_msg_userauth_failure(0, 1);
		return;
	}

	if (passwordlen > DROPBEAR_MAX_PASSWORD_LEN) {
		dropbear_log(LOG_WARNING,
				"Too-long password attempt for '%s' from %s",
				ses.authstate.pw_name,
				svr_ses.addrstring);
		send_msg_userauth_failure(0, 1);
		return;
	}

	if (testcrypt == NULL) {
		/* crypt() with an invalid salt like "!!" */
		/* SSHD4A_REQUIRED_CHANGE: changed confusing message. */
		dropbear_log(LOG_WARNING, "User account '%s' login failed",
				ses.authstate.pw_name);
		send_msg_userauth_failure(0, 1);
		return;
	}

	/* check for empty password */
	if (passwdcrypt[0] == '\0') {
		dropbear_log(LOG_WARNING, "User '%s' has blank password, rejected",
				ses.authstate.pw_name);
		send_msg_userauth_failure(0, 1);
		return;
	}

	if (constant_time_strcmp(testcrypt, passwdcrypt) == 0) {
		/* successful authentication */
		dropbear_log(LOG_NOTICE, 
				"Password auth succeeded for '%s' from %s",
				ses.authstate.pw_name,
				svr_ses.addrstring);
		send_msg_userauth_success();
	} else {
		dropbear_log(LOG_WARNING,
				"Bad password attempt for '%s' from %s",
				ses.authstate.pw_name,
				svr_ses.addrstring);
		send_msg_userauth_failure(0, 1);
	}
#ifdef SSHD4A_EXTEND_AUTHENTICATION
    free(testcrypt);
#endif /* SSHD4A_EXTEND_AUTHENTICATION */
}

#endif
