package com.hardbacknutter.sshd;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class SshdSettings {

    /** Filename used in native code. Stored in {@link SshdSettings#getDropbearDirectory}. */
    static final String DROPBEAR_ERR = "dropbear.err";
    /** Filename used in native code. Stored in {@link SshdSettings#getDropbearDirectory}. */
    static final String AUTHORIZED_KEYS = "authorized_keys";
    /** Filename used in native code. Stored in {@link SshdSettings#getDropbearDirectory}. */
    private static final String MASTER_PASSWORD = "master_password";

    private SshdSettings() {
    }

    /**
     * Typically only returns a single address, but there could be a list/mix of IPv4 and IPv6.
     * <p>
     * See <a href="https://developer.android.com/studio/run/emulator-networking>emulator</a>.
     *
     * @return a list of all IP addresses used by the device.
     */
    @SuppressWarnings("SameParameterValue")
    @NonNull
    static List<String> getHostAddresses(final int limit) {
        try {
            return Collections
                    .list(NetworkInterface.getNetworkInterfaces())
                    .stream()
                    .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                    .filter(ina -> !ina.isLoopbackAddress() && !ina.isLinkLocalAddress())
                    .map(InetAddress::getHostAddress)
                    .filter(Objects::nonNull)
//                    .map(ip -> {
//                        // strip of the scope id for IPv6 if present
//                        //noinspection ConstantConditions
//                        int i = ip.indexOf('%');
//                        return i != -1 ? ip.substring(0, i) : ip;
//                    })
                    // sorting will move IPv6 to the end of the list
                    .sorted()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (@NonNull final Exception ignore) {
            // ignore
        }

        return new ArrayList<>();
    }

    @NonNull
    static String getHomePath(@NonNull final Context context,
                              @NonNull final SharedPreferences pref) {
        String homePath = pref.getString(Prefs.HOME, null);
        if (homePath == null || !new File(homePath).exists()) {
            homePath = context.getFilesDir().getPath();
        }
        return homePath;
    }

    @NonNull
    static File getDropbearDirectory(@NonNull final Context context) {
        final File path = new File(context.getFilesDir(), ".dropbear");
        if (!path.exists()) {
            //noinspection ResultOfMethodCallIgnored
            path.mkdir();
        }
        return path;
    }


    /**
     * Read the master user + hashed password from the dropbear file.
     *
     * @param context Current context
     *
     * @return a String array with [0] the username, and [1] the hashed/base64 password.
     */
    @Nullable
    static String[] readMasterUserAndPassword(@NonNull final Context context) {
        final File path = getDropbearDirectory(context);
        final File file = new File(path, MASTER_PASSWORD);
        final List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                return lines.get(0).split(":");
            }
        } catch (@NonNull final IOException ignore) {
            // ignore
        }
        return null;
    }

    static void writeMasterUserAndPassword(@NonNull final Context context,
                                           @Nullable final String username,
                                           @Nullable final String password)
            throws IOException,
                   NoSuchAlgorithmException {

        final File path = getDropbearDirectory(context);
        final File file = new File(path, MASTER_PASSWORD);

        // No username? Remove the file.
        if (username == null || username.isBlank()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return;
        }

        // Do we have a (new) password? Create a new file overwriting any previous.
        if (password != null && !password.isBlank()) {
            //noinspection ImplicitDefaultCharsetUsage
            try (FileWriter fw = new FileWriter(file)) {
                fw.write((username + ":" + hash(password)).toCharArray());
            }
            return;
        }

        // We have a username but no password.
        // Was there a previous file?
        if (!file.exists()) {
            // ignore the username, we're done.
            return;
        }

        // We have a username but no password AND there was a previous file.
        // Retrieve the encrypted password, and rewrite the file using the new username
        // and the retrieved password.
        final String[] previous = readMasterUserAndPassword(context);
        if (previous != null && previous.length == 2) {
            //noinspection ImplicitDefaultCharsetUsage
            try (FileWriter fw = new FileWriter(file)) {
                // Rewrite the file with the new username and the previous (already encrypted)
                // and the previous (already encrypted) password
                fw.write((username + ":" + previous[1]).toCharArray());
            }
        }
    }

    /**
     * Hash with SHA-512, and convert to a base64 string.
     *
     * @param password to hash
     *
     * @return base64 string
     *
     * @throws NoSuchAlgorithmException on ...
     */
    @NonNull
    private static String hash(@NonNull final String password)
            throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        final byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    public static native void enable_public_key_auth(boolean enable);

    public static native void enable_single_use_password(boolean enable);

    public static native String getDropbearVersion();
    public static native String getOpensshVersion();
    public static native String getRsyncVersion();
}
