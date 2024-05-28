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
     * Failure to start will return a {@link Activity#RESULT_CANCELED}.
     */
    private static final String START = "com.hardbacknutter.sshd.fg.START";

    /**
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
            if (Prefs.isStartByIntentAllowed(this)) {
                final String action = intent.getAction();
                if (action != null && !action.isBlank()) {
                    switch (action) {
                        case START: {
                            if (!SshdService.isRunning()) {
                                final boolean started = vm.startService(this,
                                                                  SshdService.StartMode.ByIntent);
                                if (!started) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            }
                            break;
                        }
                        case STOP: {
                            // Only allowed to stop and quit if we were started by Intent
                            // and if the user not stopped/started the service manually
                            if (vm.getStartMode() == SshdService.StartMode.ByIntent) {
                                if (SshdService.isRunning()) {
                                    vm.stopService(this);
                                }
                                setResult(RESULT_OK);
                                finish();
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
    }
}
