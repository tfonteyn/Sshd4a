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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

public class SettingsFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    private static final String PK_SSHD_MASTER_PASSWORD = "sshd.master.password";
    private SwitchPreference pRunOnBoot;
    private SwitchPreference pRunInForeground;
    private EditTextPreference pPort;
    private EditTextPreference pPassword;
    private MainViewModel vm;
    private final OnBackPressedCallback backPressedCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    try {
                        //noinspection DataFlowIssue
                        vm.writeMasterPassword(getContext(), pPassword.getText());
                        getParentFragmentManager().popBackStack();

                    } catch (@NonNull final IOException ignore) {
                        // we should never get here... flw
                        //noinspection DataFlowIssue
                        Snackbar.make(getView(), R.string.err_failed_to_save,
                                      Snackbar.LENGTH_LONG).show();
                        getView().postDelayed(() -> getParentFragmentManager().popBackStack(),
                                              2_000);
                    }

                }
            };

    @Override
    public void onCreatePreferences(@Nullable final Bundle savedInstanceState,
                                    @Nullable final String rootKey) {
        final Context context = getContext();
        //noinspection ConstantConditions
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
        //noinspection ConstantConditions
        pPort.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        pPort.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.selectAll();
        });

        //noinspection ConstantConditions
        findPreference(Prefs.DROPBEAR_CMDLINE_OPTIONS)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        //noinspection ConstantConditions
        findPreference(Prefs.ENV_VARS)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        //noinspection ConstantConditions
        findPreference(Prefs.HOME)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        //noinspection ConstantConditions
        findPreference(Prefs.SHELL)
                .setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        pPassword = findPreference(PK_SSHD_MASTER_PASSWORD);
        //noinspection DataFlowIssue
        pPassword.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                                  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.selectAll();
        });
        pPassword.setSummaryProvider(preference -> {
            final String value = ((EditTextPreference) preference).getText();
            if (value == null || value.isEmpty()) {
                return getString(R.string.info_not_set);
            } else {
                return "********";
            }
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

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        //noinspection ConstantConditions
        vm.init(getContext());
        pPassword.setText(vm.readMasterPassword(getContext()));
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
    public void onSharedPreferenceChanged(@NonNull final SharedPreferences sharedPreferences,
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
                final int port = getPort(sharedPreferences);
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
