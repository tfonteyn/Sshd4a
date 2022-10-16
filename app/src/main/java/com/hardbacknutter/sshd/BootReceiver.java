package com.hardbacknutter.sshd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import androidx.annotation.NonNull;

public class BootReceiver
        extends BroadcastReceiver {

    public void onReceive(@NonNull final Context context,
                          @NonNull final Intent intent) {

        final String action = intent.getAction();
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
            || Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.getBoolean(Prefs.RUN_ON_BOOT, false)) {
                SshdService.startService(SshdService.Starter.OnBoot, context);
            }
        }
    }
}
