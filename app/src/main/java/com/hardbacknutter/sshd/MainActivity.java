package com.hardbacknutter.sshd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity
        extends AppCompatActivity {

    /**
     * From adb shell: {@code am start -a com.hardbacknutter.sshd.fg.START}.
     * <p>
     * Failure to start will return a {@link Activity#RESULT_CANCELED}.
     */
    private static final String START = "com.hardbacknutter.sshd.fg.START";

    /**
     * From adb shell: {@code am start -a com.hardbacknutter.sshd.fg.STOP}.
     * <p>
     * A successful stop will return a  {@link Activity#RESULT_OK}.
     */
    private static final String STOP = "com.hardbacknutter.sshd.fg.STOP";

    private ServiceViewModel vm;

    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        vm = new ViewModelProvider(this).get(ServiceViewModel.class);

        handeIntentAction(getIntent());

        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        final FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(MainFragment.TAG) == null) {
            fm.beginTransaction()
              .setReorderingAllowed(true)
              .add(R.id.fragment_container, MainFragment.class, null, MainFragment.TAG)
              .commit();
        }
    }

    @Override
    protected void onNewIntent(@NonNull final Intent intent) {
        super.onNewIntent(intent);
        handeIntentAction(intent);
    }

    private void handeIntentAction(@Nullable final Intent intent) {
        if (intent != null) {
            // Permission to start/stop by Intent must be explicitly given in the Settings.
            if (Prefs.isStartByIntentAllowed(this)) {
                final String action = intent.getAction();
                if (action != null && !action.isBlank()) {
                    switch (action) {
                        case START: {
                            startByIntent();
                            break;
                        }
                        case STOP: {
                            stopByIntent();
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void startByIntent() {
        if (SshdService.isRunning()) {
            // already running, ignore and keep running
            return;
        }

        if (vm.startService(this, SshdService.StartMode.ByIntent)) {
            // We're up and running
            return;
        }

        // Unknown failure
        setResult(RESULT_CANCELED);
        finishAndRemoveTask();
    }

    private void stopByIntent() {
        if (!SshdService.isRunning()) {
            // we're not running, ignore and quit
            setResult(RESULT_OK);
            finishAndRemoveTask();
            return;
        }

        // Only allowed to stop and quit if we were started by Intent
        if (vm.getStartMode() == SshdService.StartMode.ByIntent) {
            final boolean stopped = vm.stopService(this);
            setResult(stopped ? RESULT_OK : RESULT_CANCELED);
            // If the stop-request failed, then removing the task will kill the service.
            finishAndRemoveTask();
        }
    }
}
