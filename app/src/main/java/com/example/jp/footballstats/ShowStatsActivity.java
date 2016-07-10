package com.example.jp.footballstats;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowStatsActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private int totalGames, totalWins, totalDraws, totalLoses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.show_stats_title);
        handler.post(countTotals);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable countTotals = new Runnable() {
        @Override
        public void run() {
            totalGames = 20;
            totalWins = 8;
            totalDraws = 5;
            totalLoses = 7;
            showTotals();
        }
    };

    private void showTotals(){
        ((TextView) findViewById(R.id.show_stats_total_games)).setText(String.valueOf(totalGames));
        ((TextView) findViewById(R.id.show_stats_total_wins)).setText(String.valueOf(totalWins));
        ((TextView) findViewById(R.id.show_stats_total_draws)).setText(String.valueOf(totalDraws));
        ((TextView) findViewById(R.id.show_stats_total_loses)).setText(String.valueOf(totalLoses));
    }
}
