package com.hardbacknutter.sshd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SettingsFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    /** Not persisted, written directly to the dropbear directory. */
    private static final String PK_SSHD_AUTH_USERNAME = "sshd.authorized.username";
    /** Not persisted, written directly to the dropbear directory. */
    private static final String PK_SSHD_AUTH_PASSWORD = "sshd.authorized.password";

    private SwitchPreference pRunOnBoot;
    private SwitchPreference pRunInForeground;
    private EditTextPreference pPort;
    private EditTextPreference pAuthUsername;
    private EditTextPreference pAuthPassword;

    private final OnBackPressedCallback backPressedCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    try {
                        //noinspection DataFlowIssue
                        SshdSettings.writePasswordFile(getContext(),
                                                       pAuthUsername.getText(),
                                                       pAuthPassword.getText());
                    } catch (@NonNull final IOException | NoSuchAlgorithmException ignore) {
                        // we should never get here... flw
                        //noinspection DataFlowIssue
                        Snackbar.make(getView(), R.string.err_failed_to_save,
                                      Snackbar.LENGTH_LONG).show();
                        getView().postDelayed(() -> getParentFragmentManager().popBackStack(),
                                              2_000);
                    }

                    if (hasAtLeastOneAuthMethod()) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        new MaterialAlertDialogBuilder(getContext())
                                .setIcon(R.drawable.ic_baseline_warning_24)
                                .setTitle(R.string.warning)
                                .setMessage(R.string.warning_no_auth_methods)
                                .setCancelable(true)
                                .setNegativeButton(android.R.string.cancel, (d, which) ->
                                        d.dismiss())
                                .setPositiveButton(android.R.string.ok, (d, which) ->
                                        getParentFragmentManager().popBackStack())
                                .create()
                                .show();
                    }
                }
            };
    private boolean hasPreviousPassword;

    private boolean hasAtLeastOneAuthMethod() {
        final Context context = getContext();
        //noinspection DataFlowIssue
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String[] userAndPassword = SshdSettings.readPasswordFile(context);

        return prefs.getBoolean(Prefs.ENABLE_SINGLE_USE_PASSWORDS, true)
               || prefs.getBoolean(Prefs.ENABLE_PUBLIC_KEY_LOGIN, true)
               || (userAndPassword != null && userAndPassword.length == 2
                   && userAndPassword[0] != null && !userAndPassword[0].isBlank()
                   && userAndPassword[1] != null && !userAndPassword[1].isBlank());
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onCreatePreferences(@Nullable final Bundle savedInstanceState,
                                    @Nullable final String rootKey) {
        final Context context = getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final String homePath = prefs.getString(Prefs.HOME, null);
        if (homePath == null || !new File(homePath).exists()) {
            prefs.edit().putString(Prefs.HOME, context.getFilesDir().getPath()).apply();
        }

        setPreferencesFromResource(R.xml.preferences, rootKey);

        final Preference pUiTheme = findPreference(Prefs.UI_THEME);
        //noinspection ConstantConditions
        pUiTheme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        pUiTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            // we should never have an invalid setting in the prefs... flw
            try {
                final int mode = Integer.parseInt(String.valueOf(newValue));
                NightMode.apply(mode);
                return true;

            } catch (@NonNull final NumberFormatException ignore) {
                // ignore
            }
            return false;
        });

        pRunOnBoot = findPreference(Prefs.RUN_ON_BOOT);
        pRunInForeground = findPreference(Prefs.RUN_IN_FOREGROUND);

        pPort = findPreference(Prefs.SSHD_PORT);
        pPort.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        pPort.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.selectAll();
        });

        findPreference(Prefs.DROPBEAR_CMDLINE_OPTIONS)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        findPreference(Prefs.ENV_VARS)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        findPreference(Prefs.HOME)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        findPreference(Prefs.SHELL)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        pAuthUsername = findPreference(PK_SSHD_AUTH_USERNAME);
        pAuthUsername.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        pAuthPassword = findPreference(PK_SSHD_AUTH_PASSWORD);
        pAuthPassword.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                                  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.selectAll();
        });
        pAuthPassword.setSummaryProvider(preference -> {
            if (!hasPreviousPassword) {
                final String value = ((EditTextPreference) preference).getText();
                if (value == null || value.isEmpty()) {
                    return getString(R.string.pref_not_set);
                }
            }
            return "********";
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        getActivity().getOnBackPressedDispatcher()
                     .addCallback(getViewLifecycleOwner(), backPressedCallback);

        final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        }

        //noinspection DataFlowIssue
        final String[] previous = SshdSettings.readPasswordFile(getContext());
        if (previous != null && previous.length == 2) {
            pAuthUsername.setText(previous[0]);
            // do NOT set the text, we only have the hashed password.
            pAuthPassword.setText("");
            hasPreviousPassword = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //noinspection ConstantConditions
        getPreferenceScreen().getSharedPreferences()
                             .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        //noinspection ConstantConditions
        getPreferenceScreen().getSharedPreferences()
                             .unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull final SharedPreferences prefs,
                                          @Nullable final String key) {
        if (key == null) {
            return;
        }
        switch (key) {
            case Prefs.RUN_ON_BOOT:
            case Prefs.RUN_IN_FOREGROUND: {
                if (pRunOnBoot.isChecked() && !pRunInForeground.isChecked()) {
                    pRunInForeground.setChecked(true);
                }
                break;
            }
            case Prefs.SSHD_PORT: {
                final int port = getPort(prefs);
                if (port < 1024 || port > 32768) {
                    pPort.setText(Prefs.DEFAULT_PORT);
                    //noinspection ConstantConditions
                    Snackbar.make(getView(), R.string.err_port_number, Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;

        }
    }

    private int getPort(@NonNull final SharedPreferences pref) {
        final String s = pref.getString(Prefs.SSHD_PORT, Prefs.DEFAULT_PORT);
        try {
            return Integer.parseInt(s);
        } catch (@NonNull final Exception ignore) {
            // ignore
        }
        return 2222;
    }
}
