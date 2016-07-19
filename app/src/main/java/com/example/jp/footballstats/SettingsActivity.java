package com.example.jp.footballstats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private static String PREFS_NAME = "FootballStatsPrefs";

    private boolean isAutomaticBackupOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        isAutomaticBackupOn = settings.getBoolean("isAutomaticBackupOn", false);

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

        String lastBackup = FootballStatsDatabase.checkLastBackup();
        if (lastBackup.isEmpty()) lastBackup = getString(R.string.settings_last_backup_empty);
        TextView tv = (TextView) findViewById(R.id.setting_backup_date);
        if (tv != null) tv.setText(lastBackup);
    }

    @Override
    protected void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isAutomaticBackupOn", isAutomaticBackupOn);
        editor.apply();
    }

    private void turnOffBackup() {
        isAutomaticBackupOn = false;
    }

    private void turnOnBackup() {
        isAutomaticBackupOn = true;
    }

    public void backupNow(View view){
        boolean result = FootballStatsDatabase.backupDatabase(getBaseContext());
        //TODO change last backup date
        //TODO consider backup in new thread and callback
    }
}
