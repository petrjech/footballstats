package com.example.jp.footballstats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch backupSwitch = (Switch) findViewById(R.id.settings_backup_switch);
        if (backupSwitch != null) {
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
    }

    private void turnOffBackup() {

    }

    private void turnOnBackup() {

    }

    public void backupNow(View view){

    }
}
