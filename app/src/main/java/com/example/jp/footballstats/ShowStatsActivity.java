package com.example.jp.footballstats;

import android.content.Intent;
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
    private ArrayList<Integer> ratingChartColumns = new ArrayList<>();
    private ArrayList<Float> ratingChartColumnValues = new ArrayList<>();

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
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
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

            prepareChartData();

            RatingChartView rcv = ((RatingChartView) findViewById(R.id.rating_chart_widget));
            if (rcv != null) rcv.setChartData(ratingChartColumns, ratingChartColumnValues, averageRating);
        }
    };

    private void showTotals(){
        TextView tv = ((TextView) findViewById(R.id.show_stats_total_games));
        if (tv != null) tv.setText(String.valueOf(totalGames));
        tv = ((TextView) findViewById(R.id.show_stats_total_wins));
        if (tv != null) tv.setText(String.valueOf(totalWins));
        tv = ((TextView) findViewById(R.id.show_stats_total_draws));
        if (tv != null) tv.setText(String.valueOf(totalDraws));
        tv = ((TextView) findViewById(R.id.show_stats_total_losses));
        if (tv != null) tv.setText(String.valueOf(totalLosses));
    }

    private void showRatingStats(){
        TextView tv = ((TextView) findViewById(R.id.show_stats_rating_average));
        if (tv != null) tv.setText(String.valueOf(averageRating));
        if (wins.size() > 0) {
            tv = ((TextView) findViewById(R.id.show_stats_best_win));
            if (tv != null) tv.setText(String.valueOf(wins.get(wins.size() - 1).toString()));
        }
        if (losses.size() > 0) {
            tv = ((TextView) findViewById(R.id.show_stats_worst_loss));
            if (tv != null) tv.setText(String.valueOf(losses.get(0).toString()));
        }
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

    private void prepareChartData(){
        if (wins.size() == 0 || losses.size() == 0) return;
        int column = Math.min(losses.get(0), wins.get(0)) / 100;
        column *= 100;
        int winsPointer = 0;
        int lossesPointer = 0;
        while (winsPointer < wins.size() || lossesPointer < losses.size()) {
            int winsCounter = 0;
            int lossesCounter = 0;
            while (winsPointer < wins.size() && wins.get(winsPointer) < column + 100) {
                winsCounter++;
                winsPointer++;
            }
            while (lossesPointer < losses.size() && losses.get(lossesPointer) < column + 100) {
                lossesCounter++;
                lossesPointer++;
            }
            if (winsCounter + lossesCounter == 0) {
                ratingChartColumnValues.add(null);
            } else {
                ratingChartColumnValues.add(((float) winsCounter) / (winsCounter + lossesCounter));
            }
            ratingChartColumns.add(column);
            column += 100;
        }
    }
}
