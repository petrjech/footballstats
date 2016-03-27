package com.example.jp.footballstats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayerStatsActivity extends AppCompatActivity {

    private final static int ADD_GAME_REQUEST = 0;

    private ArrayList<Game> gameArrayList = new ArrayList<>();
    private GameListAdapter gameListAdapter;
    private long playerID;
    private String player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        playerID = intent.getLongExtra("id", 0);
        player = intent.getStringExtra("playerName");

        setTitle(player + " " + getString(R.string.player_stats_title_activity));

        gameListAdapter = new GameListAdapter(this, gameArrayList);

        ListView gameListView = (ListView) findViewById(R.id.game_list);
        gameListView.setAdapter(gameListAdapter);

        populateGames();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        //ToDo add menu support
        return super.onOptionsItemSelected(item);
    }

    public void addGame(View view){
        Intent intent = new Intent(getBaseContext(), AddGameActivity.class);
        intent.putExtra("id", playerID);
        intent.putExtra("playerName", player);
        startActivityForResult(intent, ADD_GAME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_GAME_REQUEST) {
            if (resultCode == RESULT_OK) {
                populateGames();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
        }
    }

    private void populateGames(){
        StatsDataAccessObject statsDAO = StatsDataAccessObject.getInstance(this);
        statsDAO.searchGames(playerID, gameArrayList);
        gameListAdapter.notifyDataSetChanged();
    }
}
