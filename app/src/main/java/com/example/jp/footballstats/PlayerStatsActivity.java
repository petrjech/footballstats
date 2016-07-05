package com.example.jp.footballstats;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
        getMenuInflater().inflate(R.menu.menu_player_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_delete_player:
                showDialogDeletePlayer();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void showDialogDeletePlayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerStatsActivity.this);
        builder.setTitle(R.string.dialog_delete_player_title);
        builder.setMessage(getString(R.string.dialog_delete_player_message) + player + " ?");
        builder.setPositiveButton(R.string.dialog_delete_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePlayer();
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
        builder.setIcon(android.R.drawable.ic_delete);
        builder.show();
    }

    private void deletePlayer() {
        StatsDataAccessObject statsDAO;
        statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
        statsDAO.deletePlayer(playerID);
    }
}