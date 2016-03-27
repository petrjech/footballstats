package com.example.jp.footballstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int OPEN_PLAYER_REQUEST = 0;

    private ArrayList<Player> searchPlayerResults = new ArrayList<>();
    private PlayerListAdapter playerListAdapter;
    private String searchPlayerCache = "";

    static String displayDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences("preferences", 0);
        displayDateFormat = settings.getString("dateFormat", "dd.MM.yyyy");

        playerListAdapter = new PlayerListAdapter(this, searchPlayerResults);

        ListView search_result_list = (ListView) findViewById(R.id.search_result_list);
        search_result_list.setAdapter(playerListAdapter);

        search_result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = (Player)parent.getItemAtPosition(position);
                openPlayerGameStats(player);
            }
        });

        EditText search_input_widget = (EditText) findViewById(R.id.search_input);
        search_input_widget.addTextChangedListener(searchPlayerWatcher);
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

    public void addPlayer(View view){
        EditText search_input_widget = (EditText) findViewById(R.id.search_input);
        String playerName = search_input_widget.getText().toString().trim();
        if (playerName.isEmpty()){
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

    private void openPlayerGameStats(Player player){
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
}