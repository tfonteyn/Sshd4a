package com.hardbacknutter.sshd;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import com.hardbacknutter.sshd.databinding.DialogAboutBinding;
import com.hardbacknutter.sshd.databinding.FragmentMainBinding;

public class MainFragment
        extends Fragment {

    public static final String TAG = "MainFragment";
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (!isGranted) {
                            setAskPermission(false);
                        }
                    });
    private ServiceViewModel vm;
    /**
     * Listen for broadcasts from the service informing us about any changes.
     * We simply react by updating the UI.
     */
    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(@NonNull final Context context,
                              @NonNull final Intent intent) {
            vm.updateUI();
        }
    };
    private FragmentMainBinding vb;
    private ActivityResultLauncher<String> authKeysImportLauncher;
    private ToolbarMenuProvider toolbarMenuProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        vb = FragmentMainBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {

        final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbarMenuProvider = new ToolbarMenuProvider();
        toolbar.addMenuProvider(toolbarMenuProvider, getViewLifecycleOwner());

        //noinspection DataFlowIssue
        vm = new ViewModelProvider(getActivity()).get(ServiceViewModel.class);
        vm.onLogUpdate().observe(getViewLifecycleOwner(), output -> {
            // We always replace the WHOLE content. TODO: receive and append updates only
            vb.log.setText(String.join("\n", output));
            vb.logScroller.post(() -> vb.logScroller.fullScroll(ScrollView.FOCUS_DOWN));
        });
        vm.onServiceStateChanged().observe(getViewLifecycleOwner(), isRunning ->
                toolbarMenuProvider.updateStartButton(isRunning));

        authKeysImportLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), this::onImportAuthKeys);
    }

    @Override
    public void onResume() {
        super.onResume();

        // This is quite essential, without this permission the user cannot really use rsync/sftp
        if (!Environment.isExternalStorageManager()) {
            //noinspection ConstantConditions
            new MaterialAlertDialogBuilder(getContext())
                    .setIcon(R.drawable.security_24px)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.msg_request_files_management)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.cancel, (d, which) -> d.dismiss())
                    .setPositiveButton(android.R.string.ok, (d, which) -> {
                        // TODO: the dialog does not dismiss first time??
                        d.dismiss();
                        startActivity(new Intent(
                                Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                    })
                    .create()
                    .show();
        }

        // This is optional and only needed from Android 13 up
        if (Build.VERSION.SDK_INT >= 33) {
            //noinspection ConstantConditions
            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                if (isAskPermission()) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }

        //noinspection ConstantConditions
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                messageReceiver, new IntentFilter(SshdService.SERVICE_UI_REQUEST));

        populateNetworkAddressList();

        final boolean isRunning = SshdService.isRunning();

        if (Prefs.isRunOnAppStart(getContext())
            && !isRunning) {
            vm.startService(getContext(), SshdService.StartMode.ByUser);

        } else if (isRunning) {
            vm.startUpdateThread(getContext());
        }
    }

    @Override
    public void onPause() {
        vm.cancelUpdateThread();
        //noinspection ConstantConditions
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (vm != null) {
            //noinspection ConstantConditions
            vm.stopService(getContext());
        }
        super.onDestroy();
    }

    private void populateNetworkAddressList() {
        // limit 3... assuming a max of 1 mobile + 1 wifi + 1 eth
        final List<String> ipList = SshdSettings.getHostAddresses(3);
        if (ipList.isEmpty()) {
            // should never happen... flw
            vb.ip.setText(R.string.err_no_ip);
        } else {
            vb.ip.setText(String.join("\n", ipList));
            //noinspection ConstantConditions
            vb.port.setText(Prefs.getPort(getContext()));
        }
    }

    private void onImportAuthKeys(@Nullable final Uri uri) {
        if (uri != null) {
            //noinspection ConstantConditions
            final String error = vm.importAuthKeys(getContext(), uri);
            if (error != null) {
                // note that the state of the current key file is unknown at this point
                new MaterialAlertDialogBuilder(getContext())
                        .setIcon(R.drawable.error_24px)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setMessage(error)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss())
                        .create()
                        .show();
            }
        }
    }

    private boolean isAskPermission() {
        //noinspection DataFlowIssue
        return PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getBoolean(Prefs.UI_NOTIFICATION_ASK_PERMISSION, true);
    }

    @SuppressWarnings("SameParameterValue")
    private void setAskPermission(final boolean flag) {
        //noinspection DataFlowIssue
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .edit()
                .putBoolean(Prefs.UI_NOTIFICATION_ASK_PERMISSION, flag)
                .apply();
    }

    private void showAbout() {
        final Context context = getContext();
        String version;
        try {
            //noinspection ConstantConditions
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;

        } catch (@NonNull final Exception e) {
            version = getString(R.string.err_no_version);
        }

        final DialogAboutBinding dvb = DialogAboutBinding.inflate(getLayoutInflater());
        dvb.version.setText(getString(R.string.about_versions,
                                      SshdService.getDropbearVersion(),
                                      SshdService.getRsyncVersion(),
                                      SshdService.getOpensshVersion()));

        new MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.info_24px)
                .setTitle(getString(R.string.about_title, version))
                .setView(dvb.getRoot())
                .setCancelable(true)
                .setNeutralButton(R.string.lbl_github_project, (d, which) -> startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                   Uri.parse(getString(R.string.github_project_url)))))
                .setPositiveButton(android.R.string.ok, (d, which) -> d.dismiss())
                .create()
                .show();
    }

    private void showResetKeys() {
        final Context context = getContext();
        //noinspection ConstantConditions
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.warning_24px)
                .setTitle(R.string.lbl_reset_keys)
                .setMessage(R.string.confirm_reset_keys)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (d, which) -> d.dismiss())
                .setPositiveButton(android.R.string.ok, (d, which) -> vm.deleteAuthKeys(context))
                .create()
                .show();
    }

    @SuppressWarnings("SameParameterValue")
    private void replaceFragment(@NonNull final Class<? extends Fragment> fragmentClass,
                                 @NonNull final String tag) {
        final FragmentManager fm = getParentFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
              .addToBackStack(tag)
              .setReorderingAllowed(true)
              .replace(R.id.fragment_container, fragmentClass, null, tag)
              .commit();
        }
    }

    private class ToolbarMenuProvider
            implements MenuProvider {

        private Button startButton;

        @Override
        public void onCreateMenu(@NonNull final Menu menu,
                                 @NonNull final MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.main_menu, menu);

            final MenuItem menuItem = menu.findItem(R.id.start_action);
            //noinspection DataFlowIssue
            startButton = menuItem.getActionView().findViewById(R.id.btn_start);
            startButton.setOnClickListener(v -> onMenuItemSelected(menuItem));
        }

        void updateStartButton(final boolean isRunning) {
            if (isRunning) {
                startButton.setText(R.string.lbl_stop);
                startButton.setTextColor(getTextColor(R.attr.stopButtonColor));
            } else {
                startButton.setText(R.string.lbl_start);
                startButton.setTextColor(getTextColor(R.attr.startButtonColor));
            }
        }

        @ColorInt
        private int getTextColor(@AttrRes final int colorAttrId) {
            //noinspection DataFlowIssue
            final TypedArray a = getContext()
                    .obtainStyledAttributes(new int[]{colorAttrId});
            try {
                return a.getColor(0, 0);
            } finally {
                a.recycle();
            }
        }

        @Override
        public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
            final int itemId = menuItem.getItemId();
            if (itemId == R.id.start_action) {
                if (SshdService.isRunning()) {
                    //noinspection ConstantConditions
                    vm.stopService(getContext());
                } else {
                    //noinspection ConstantConditions
                    if (!vm.startService(getContext(), SshdService.StartMode.ByUser)) {
                        //noinspection ConstantConditions
                        Snackbar.make(getView(), R.string.err_service_failed_to_start,
                                      Snackbar.LENGTH_LONG).show();
                    }
                }
            } else if (itemId == R.id.settings) {
                replaceFragment(SettingsFragment.class, SettingsFragment.TAG);
                return true;
            } else if (itemId == R.id.menu_import_keys) {
                authKeysImportLauncher.launch("*/*");
                return true;
            } else if (itemId == R.id.menu_reset_keys) {
                showResetKeys();
                return true;
            } else if (itemId == R.id.about) {
                showAbout();
                return true;
            }
            return false;
        }
    }
}
