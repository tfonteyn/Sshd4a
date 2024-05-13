package com.hardbacknutter.sshd;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserPasswordTest {

    private static final String MY_USER = "myuser";
    private static final String MY_USER2 = "myuser2";

    private static final String MY_PASSWORD = "mypassword";
    private static final String MY_PASSWORD2 = "mypassword2";

    private static final String BASE64_MY_PASSWORD =
            "ozb2cQgPv08qIw8xNWDd8NDBLfzxdB5J6HIqI0ZzA33E" +
            "k8qo0pHYAl9xCJ1jzqgJzIrlPlsXBUgGg32+QJnEyg" +
            "==";
    private static final String BASE64_MY_PASSWORD2 =
            "+7prmna8hFSfs1N0LOS3BheAqIFQLQlQoYqlIKw/ckbo" +
            "yo/Q+xQHOGz4HeVrWG1R6/EdmfAM60qAEtXpra1T1g" +
            "==";

    /**
     * Not an actual test, we're just creating the base64 and dumping it to the log.
     */
    @Test
    public void dumpPassword()
            throws NoSuchAlgorithmException {
        showBase64(MY_PASSWORD);
        showBase64(MY_PASSWORD2);
    }

    private void showBase64(@NonNull final String password)
            throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        final byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder();
        for (final byte b : digest) {
            sb.append(String.format("%02X ", b));
        }
        Log.d(password, sb.toString());
        Log.d(password, Base64.getEncoder().encodeToString(digest));
    }

    @Test
    public void pw()
            throws IOException, NoSuchAlgorithmException {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String[] userAndPassword;
        File file;

        final File dropbearDirectory = SshdSettings.getDropbearDirectory(context);
        assertTrue(dropbearDirectory.exists());

        // Remove any existing
        SshdSettings.writeMasterUserAndPassword(context, null, null);
        file = new File(dropbearDirectory, SshdSettings.MASTER_PASSWORD);
        assertFalse(file.exists());

        // Create first time
        SshdSettings.writeMasterUserAndPassword(context, MY_USER, MY_PASSWORD);

        userAndPassword = SshdSettings.readMasterUserAndPassword(context);
        assertNotNull(userAndPassword);
        assertEquals(2, userAndPassword.length);
        assertEquals(MY_USER, userAndPassword[0]);
        assertEquals(BASE64_MY_PASSWORD, userAndPassword[1]);

        // Replace the user
        SshdSettings.writeMasterUserAndPassword(context, MY_USER2, null);
        userAndPassword = SshdSettings.readMasterUserAndPassword(context);
        assertNotNull(userAndPassword);
        assertEquals(2, userAndPassword.length);
        assertEquals(MY_USER2, userAndPassword[0]);
        assertEquals(BASE64_MY_PASSWORD, userAndPassword[1]);

        // Replace the password
        SshdSettings.writeMasterUserAndPassword(context, MY_USER2, MY_PASSWORD2);
        userAndPassword = SshdSettings.readMasterUserAndPassword(context);
        assertNotNull(userAndPassword);
        assertEquals(2, userAndPassword.length);
        assertEquals(MY_USER2, userAndPassword[0]);
        assertEquals(BASE64_MY_PASSWORD2, userAndPassword[1]);

        // Remove known existing
        SshdSettings.writeMasterUserAndPassword(context, null, null);
        file = new File(dropbearDirectory, SshdSettings.MASTER_PASSWORD);
        assertFalse(file.exists());
    }
}
