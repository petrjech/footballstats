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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayerStatsActivity extends AppCompatActivity {

    private final static int ADD_GAME_REQUEST = 0;
    private final static int EDIT_GAME_REQUEST = 1;

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

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Game game = (Game) parent.getItemAtPosition(position);
                showGameDialog(game);
            }
        });

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

    public void addGame(View view) {
        Intent intent = new Intent(getBaseContext(), AddGameActivity.class);
        intent.putExtra("action", "add");
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
        if (requestCode == EDIT_GAME_REQUEST) {
            if (resultCode == RESULT_OK) {
                populateGames();
            }
        }
    }

    private void populateGames() {
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
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.show();
    }

    private void deletePlayer() {
        StatsDataAccessObject statsDAO;
        statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
        statsDAO.deletePlayer(playerID);
    }

    private void showGameDialog(final Game game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerStatsActivity.this);
        builder.setTitle(R.string.dialog_game_title);
        builder.setMessage(R.string.dialog_game_message);
        builder.setPositiveButton(R.string.dialog_game_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteGame(game);
            }
        });
        builder.setNeutralButton(R.string.dialog_game_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editGame(game);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.show();
    }

    private void deleteGame(Game game) {
        StatsDataAccessObject statsDAO;
        statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
        statsDAO.deleteGame(game);
        populateGames();
    }

    private void editGame(final Game game) {
        Intent intent = new Intent(getBaseContext(), AddGameActivity.class);
        intent.putExtra("action", "edit");
        intent.putExtra("id", playerID);
        intent.putExtra("playerName", player);
        intent.putExtra("date", game.getDate());
        intent.putExtra("elo", game.getElo());
        intent.putExtra("game_id", game.getGameID());
        intent.putExtra("note", game.getNote());
        intent.putExtra("result", game.getResult());
        startActivityForResult(intent, EDIT_GAME_REQUEST);
    }
}