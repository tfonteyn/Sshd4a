package com.hardbacknutter.sshd;

import android.app.Application;

import com.hardbacknutter.sshd.utils.theme.NightMode;

public class App
        extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NightMode.init(this);
    }

    //TODO: implement a version based migration method similar to how database get upgraded.
    // 2024-05-22: remove the supersu preference key
    // future: rename the password file

//    void migrate(@NonNull final Context context) {
//
//        PreferenceManager.getDefaultSharedPreferences(context)
//                         .edit()
//                         .remove("rsyncbuffer")
//                         .apply();
//
//        final File path = SshdSettings.getDropbearDirectory(context);
//        // I should have named it the "smurf_password" ...
//        final File legacyFile = new File(path, "master_password");
//        if (legacyFile.exists()) {
//            final File file = new File(path, SshdSettings.AUTHORIZED_USERS);
//            // It's extremely unlikely that the rename fails
//            if (!legacyFile.renameTo(file)) {
//                // but in the name of paranoia.... we delete it to make sure.
//                // If the delete fails, then the user must have manually protected it.
//                // We'll ignore it...
//                //noinspection ResultOfMethodCallIgnored
//                legacyFile.delete();
//            }
//        }
//    }
}
