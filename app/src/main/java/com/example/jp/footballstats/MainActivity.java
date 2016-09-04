package com.example.jp.footballstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jp.footballstats.resources.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static int OPEN_PLAYER_REQUEST = 0;
    private final static long BACKUP_START_DELAY = 1000;

    private ArrayList<Player> searchPlayerResults = new ArrayList<>();
    private PlayerListAdapter playerListAdapter;
    private String searchPlayerCache = "";

    private Handler handlerAutomaticBackup = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playerListAdapter = new PlayerListAdapter(this, searchPlayerResults);

        ListView search_result_list = (ListView) findViewById(R.id.search_result_list);
        search_result_list.setAdapter(playerListAdapter);

        search_result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = (Player) parent.getItemAtPosition(position);
                openPlayerGameStats(player);
            }
        });

        EditText search_input_widget = (EditText) findViewById(R.id.search_input);
        search_input_widget.addTextChangedListener(searchPlayerWatcher);

        checkAutomaticBackup();
    }

    private void checkAutomaticBackup() {
        SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME, 0);
        boolean isAutomaticBackupOn = settings.getBoolean(Preferences.IS_AUTOMATIC_BACKUP_ON, false);
        if (isAutomaticBackupOn) {
            Date lastBackup = FootballStatsDatabase.checkLastBackup(getBaseContext());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, - Preferences.BACKUP_FREQUENCY_IN_DAYS);
            Date dateToCompare = cal.getTime();
            if (lastBackup == null || lastBackup.before(dateToCompare)) {
                handlerAutomaticBackup.postDelayed(startBackup , BACKUP_START_DELAY);
            }
        }
    }

    private Runnable startBackup = new Runnable() {
        @Override
        public void run() {
            FootballStatsDatabase.backupDatabase(getBaseContext(), this);
        }
    };

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
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.show_stats) {
            showStats();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addPlayer(View view) {
        EditText search_input_widget = (EditText) findViewById(R.id.search_input);
        String playerName = search_input_widget.getText().toString().trim();
        if (playerName.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.main_add_player_empty), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        StatsDataAccessObject statsDAO = StatsDataAccessObject.getInstance(this);
        boolean playerExists = statsDAO.containsPlayer(playerName);
        if (playerExists) {
            Toast toast = Toast.makeText(getApplicationContext(), playerName + " " + getString(R.string.main_add_player_exists), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        Player newPlayer = statsDAO.createPlayer(playerName);
        openPlayerGameStats(newPlayer);
    }

    private void openPlayerGameStats(Player player) {
        Intent intent = new Intent(getBaseContext(), PlayerStatsActivity.class);
        intent.putExtra("id", player.getId());
        intent.putExtra("playerName", player.getName());
        startActivityForResult(intent, OPEN_PLAYER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_PLAYER_REQUEST) {
            if (resultCode == RESULT_OK) {
                EditText search_input_widget = (EditText) findViewById(R.id.search_input);
                search_input_widget.setText("");
            }
        }
    }

    private TextWatcher searchPlayerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable searchPlayerString) {
            //Log.d("afterTextChanged", "enter");
            String search = searchPlayerString.toString().trim();
            if (search.equals(searchPlayerCache)) return;

            if (search.length() == 0) {
                searchPlayerResults.clear();
                searchPlayerCache = "";
                playerListAdapter.notifyDataSetChanged();
                return;
            }
            searchPlayerCache = search;
            StatsDataAccessObject statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
            statsDAO.searchPlayer(search, searchPlayerResults);
            playerListAdapter.notifyDataSetChanged();
        }
    };

    private void showStats() {
        Intent intent = new Intent(getBaseContext(), ShowStatsActivity.class);
        startActivity(intent);
    }
}