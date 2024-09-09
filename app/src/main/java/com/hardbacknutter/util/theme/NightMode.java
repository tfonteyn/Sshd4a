/*
 * @Copyright 2018-2022 HardBackNutter
 * @License GNU General Public License
 *
 * This file is part of NeverTooManyBooks.
 *
 * NeverTooManyBooks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NeverTooManyBooks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NeverTooManyBooks. If not, see <http://www.gnu.org/licenses/>.
 */
package com.hardbacknutter.util.theme;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.math.MathUtils;
import androidx.preference.PreferenceManager;

public final class NightMode {

    /**
     * The {@link NightMode} setting.
     * <p>
     * {@code int}
     * <p>
     * Note that the string is factually incorrect due to historical reasons.
     * Changing it is not worth the effort.
     *
     * @see NightMode
     */
    public static final String PK_UI_THEME_MODE = "ui.theme";

    /**
     * We're not using the actual {@link AppCompatDelegate} mode constants
     * due to the devices-setting being different depending on the OS version.
     */
    private static final int[] NIGHT_MODES = {
            // follow the system setting
            Build.VERSION.SDK_INT >= 29 ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                                        : AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
            // light
            AppCompatDelegate.MODE_NIGHT_NO,
            // dark
            AppCompatDelegate.MODE_NIGHT_YES
    };

    private NightMode() {
    }

    /**
     * Init the user's preferred NightMode.
     * <p>
     * Must be called from {@link Application#onCreate()}.
     *
     * @param context Current context
     */
    public static void init(@NonNull final Context context) {
        apply(getSetting(context));
    }

    /**
     * Get the current preference setting.
     *
     * @param context Current context
     *
     * @return the stored index into {@link #NIGHT_MODES}
     */
    public static int getSetting(@NonNull final Context context) {
        return getIntListPref(context, PK_UI_THEME_MODE, 0);
    }

    /**
     * Apply the user's preferred NightMode.
     * <p>
     * To be called when the user changes the preference.
     *
     * @param mode index into {@link #NIGHT_MODES}
     */
    public static void apply(@IntRange(from = 0, to = 2) final int mode) {
        AppCompatDelegate.setDefaultNightMode(NIGHT_MODES[MathUtils.clamp(mode, 0, 2)]);
    }

    @SuppressWarnings("SameParameterValue")
    private static int getIntListPref(@NonNull final Context context,
                                      @NonNull final String key,
                                      final int defValue) {
        final String value = PreferenceManager.getDefaultSharedPreferences(context)
                                              .getString(key, null);
        if (value == null || value.isEmpty()) {
            return defValue;
        }

        // we should never have an invalid setting in the prefs... flw
        try {
            return Integer.parseInt(value);
        } catch (@NonNull final NumberFormatException ignore) {
            return defValue;
        }
    }
}
