package com.example.jp.footballstats;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jp.footballstats.resources.Preferences;

import java.util.Date;


public class SettingsActivity extends AppCompatActivity {

    private boolean isAutomaticBackupOn;
    private boolean initRestoreDatabase = false;
    private Handler handler = new Handler();
    private boolean restoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME, 0);
        isAutomaticBackupOn = settings.getBoolean(Preferences.IS_AUTOMATIC_BACKUP_ON, false);

        Switch backupSwitch = (Switch) findViewById(R.id.settings_backup_switch);
        if (backupSwitch != null) {
            backupSwitch.setChecked(isAutomaticBackupOn);
            backupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        turnOnBackup();
                    } else {
                        turnOffBackup();
                    }
                }
            });
        }

        Date lastBackup = FootballStatsDatabase.checkLastBackup(getBaseContext());
        String lastBackupString;
        if (lastBackup == null) {
            lastBackupString = getString(R.string.settings_last_backup_empty);
            restoreButton = false;
        } else {
            lastBackupString = Preferences.getFormatedDateTime(lastBackup);
            restoreButton = true;
        }
        TextView tv = (TextView) findViewById(R.id.setting_backup_date);
        if (tv != null) tv.setText(lastBackupString);

        toggleRestoreButton(restoreButton);
    }

    private void toggleRestoreButton(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.restore_button);
        button.setEnabled(isEnabled);
    }

    @Override
    protected void onStop(){
        super.onStop();
        savePreferences(false);
    }

    private void savePreferences(boolean saveInitDatabaseRestore) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (saveInitDatabaseRestore) {
            editor.putBoolean(Preferences.INIT_DATABASE_RESTORE, initRestoreDatabase);
        }
        editor.putBoolean(Preferences.IS_AUTOMATIC_BACKUP_ON, isAutomaticBackupOn);
        editor.apply();
    }

    private void turnOffBackup() {
        isAutomaticBackupOn = false;
    }

    private void turnOnBackup() {
        isAutomaticBackupOn = true;
    }

    public void backupNow(View view){
        handler.postDelayed(startBackup, 100L);
    }

    public void showRestoreDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(R.string.dialog_restore_database_title);
        builder.setMessage(getString(R.string.dialog_restore_database_message));
        builder.setPositiveButton(R.string.dialog_restore_database_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                restoreNow();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    private void restoreNow() {
        initRestoreDatabase = true;
        savePreferences(true);

        Context context = getBaseContext();
        Intent mStartActivity = new Intent(getBaseContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

        this.finishAffinity();
    }

    private Runnable startBackup = new Runnable() {
        @Override
        public void run() {
            boolean resultOK = FootballStatsDatabase.backupDatabase(getBaseContext(), this);

            if (resultOK) {
                TextView tv = (TextView) findViewById(R.id.setting_backup_date);
                if (tv != null) tv.setText(Preferences.getFormatedDateTime(new Date()));
                if (!restoreButton) {
                    restoreButton = true;
                    toggleRestoreButton(restoreButton);
                }
            }
        }
    };
}
