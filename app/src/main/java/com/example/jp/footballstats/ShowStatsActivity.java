package com.example.jp.footballstats;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ShowStatsActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private int totalGames, totalWins, totalDraws, totalLosses, averageRating;
    private ArrayList<Integer> losses = new ArrayList<>();
    private ArrayList<Integer> draws = new ArrayList<>();
    private ArrayList<Integer> wins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.show_stats_title);
        handler.postDelayed(countTotals, 100L);
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
            StatsDataAccessObject statsDAO;
            statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
            statsDAO.getGameResults(wins, draws, losses);
            totalWins = wins.size();
            totalDraws = draws.size();
            totalLosses = losses.size();
            totalGames = totalLosses + totalDraws + totalWins;
            showTotals();

            Collections.sort(wins);
            Collections.sort(losses);

            averageRating = getPlayerAverageRating();
            showRatingStats();
        }
    };

    private void showTotals(){
        ((TextView) findViewById(R.id.show_stats_total_games)).setText(String.valueOf(totalGames));
        ((TextView) findViewById(R.id.show_stats_total_wins)).setText(String.valueOf(totalWins));
        ((TextView) findViewById(R.id.show_stats_total_draws)).setText(String.valueOf(totalDraws));
        ((TextView) findViewById(R.id.show_stats_total_losses)).setText(String.valueOf(totalLosses));
    }

    private void showRatingStats(){
        ((TextView) findViewById(R.id.show_stats_rating_average)).setText(String.valueOf(averageRating));
        if (wins.size() > 0) ((TextView) findViewById(R.id.show_stats_best_win)).setText(String.valueOf(wins.get(wins.size() - 1).toString()));
        if (losses.size() > 0) ((TextView) findViewById(R.id.show_stats_worst_loss)).setText(String.valueOf(losses.get(0).toString()));
    }

    static int findLowestHigherElo(ArrayList<Integer> list, int elo) {
        int size = list.size();
        if (size < 2) return 0;
        if (list.get(0) > elo) return 0;
        int floor = 0;
        int ceiling = size - 1;
        int pointer;
        int value;
        while (floor < ceiling - 1) {
            pointer = (floor + ceiling) / 2;
            value = list.get(pointer);
            if (value <= elo) {
                floor = pointer;
            } else {
                ceiling = pointer;
            }
        }
        return ceiling;
    }

    static int findHighestLowerElo(ArrayList<Integer> list, int elo) {
        int size = list.size();
        if (size < 2) return 0;
        if (list.get(size - 1) < elo) return size - 1;
        int floor = 0;
        int ceiling = size - 1;
        int pointer;
        int value;
        while (floor < ceiling - 1) {
            pointer = (floor + ceiling) / 2;
            value = list.get(pointer);
            if (value < elo) {
                floor = pointer;
            } else {
                ceiling = pointer;
            }
        }
        return floor;
    }

    int getPlayerAverageRating() {
        int winsSize = wins.size();
        int lossesSize = losses.size();
        if (winsSize < 1 || lossesSize < 1) return 0;
        int floor = losses.get(0);
        int ceiling = wins.get(winsSize - 1);
        int lastFloor = 0, lastCeiling = 0, pointer = 0;
        int overFlowCheck = Math.min(winsSize, lossesSize);
        while (floor < ceiling) {
            pointer++;
            if (pointer == overFlowCheck) return 0;
            lastFloor = floor;
            lastCeiling = ceiling;
            floor = losses.get(pointer);
            ceiling = wins.get(winsSize - pointer - 1);
        }
        return (lastFloor + lastCeiling) / 2;
    }
}
