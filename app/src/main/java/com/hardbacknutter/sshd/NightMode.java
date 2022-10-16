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
package com.hardbacknutter.sshd;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.math.MathUtils;
import androidx.preference.PreferenceManager;

public final class NightMode {

    private static final int[] NIGHT_MODES = {
            // day-night
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            // light
            AppCompatDelegate.MODE_NIGHT_NO,
            // dark
            AppCompatDelegate.MODE_NIGHT_YES
    };

    private NightMode() {
    }

    /**
     * Apply the user's preferred NightMode.
     * Called at startup.
     *
     * @param context Current context
     */
    public static void apply(@NonNull final Context context) {
        apply(getIntListPref(context, Prefs.UI_THEME, 0));
    }

    /**
     * Apply the user's preferred NightMode.
     * Called when the user changes the preference.
     *
     * @param mode index into {@link #NIGHT_MODES}
     */
    public static void apply(@IntRange(from = 0, to = 2) final int mode) {
        AppCompatDelegate.setDefaultNightMode(NIGHT_MODES[MathUtils.clamp(mode, 0, 2)]);
    }

    public static int getIntListPref(@NonNull final Context context,
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
