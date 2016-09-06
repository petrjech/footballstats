package com.example.jp.footballstats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddGameActivity extends AppCompatActivity {

    private Game game;
    private boolean gameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        gameEdit = intent.getStringExtra("action").equals("edit");

        if (gameEdit) {
            game = new Game(intent.getLongExtra("game_id", 0L));
            game.setDate(intent.getStringExtra("date"));
            game.setElo(intent.getIntExtra("elo", 0));
            game.setNote(getIntent().getStringExtra("note"));
            game.setResult(intent.getIntExtra("result", 0));

            EditText editTextElo = (EditText) findViewById(R.id.add_game_edit_elo);
            assert editTextElo != null;
            editTextElo.setText(String.valueOf(game.getElo()));

            if (!game.getNote().isEmpty()) {
                EditText editTextNote = (EditText) findViewById(R.id.add_game_edit_note);
                assert editTextNote != null;
                editTextNote.setText(game.getNote());
            }

            switch (game.getResult()) {
                case 0:
                    RadioButton radioButton0 = (RadioButton) findViewById(R.id.add_game_result_0);
                    assert radioButton0 != null;
                    radioButton0.setChecked(true);
                    break;
                case 1:
                    RadioButton radioButton1 = (RadioButton) findViewById(R.id.add_game_result_1);
                    assert radioButton1 != null;
                    radioButton1.setChecked(true);
                    break;
                case R.id.add_game_result_2:
                    RadioButton radioButton2 = (RadioButton) findViewById(R.id.add_game_result_2);
                    assert radioButton2 != null;
                    radioButton2.setChecked(true);
                    break;
            }
        } else {
            game = new Game();
        }

        game.setPlayerID(intent.getLongExtra("id", 0));
        String playerName = intent.getStringExtra("playerName");

        setTitle(playerName + "  " + getString(R.string.add_game_title_activity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_game, menu);
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

    public void submitGame(@SuppressWarnings("UnusedParameters") View view) {

        if (!setGame()) return;

        StatsDataAccessObject statsDAO;
        statsDAO = StatsDataAccessObject.getInstance(getApplicationContext());
        if (gameEdit) {
            statsDAO.updateGame(game);
        } else {
            statsDAO.saveGame(game);
        }

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean setGame() {

        if (!setGameResult()) return false;

        if (!setGameElo()) return false;

        setGameDate();

        setGameNote();

        return true;
    }

    private boolean setGameResult() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.add_game_result_group);
        assert radioGroup != null;
        int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButton < 0) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.add_game_result_error), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        switch (checkedRadioButton) {
            case R.id.add_game_result_2:
                game.setResult(2);
                break;
            case R.id.add_game_result_1:
                game.setResult(1);
                break;
            case R.id.add_game_result_0:
                game.setResult(0);
                break;
        }
        return true;
    }

    private boolean setGameElo() {
        EditText editText = (EditText) findViewById(R.id.add_game_edit_elo);
        assert editText != null;
        String eloString = editText.getText().toString();
        if (eloString.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.add_game_elo_error_empty), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        int elo;
        try {
            elo = Integer.parseInt(eloString);
        } catch (NumberFormatException nfe) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.add_game_elo_error_not_number), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        game.setElo(elo);
        return true;
    }

    private void setGameDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(StatsDataAccessObject.DATE_FORMAT, Locale.US);
        String date = sdf.format(Calendar.getInstance().getTime());
        game.setDate(date);
    }

    private void setGameNote() {
        EditText editText = (EditText) findViewById(R.id.add_game_edit_note);
        assert editText != null;
        String note = editText.getText().toString();
        game.setNote(note);
    }
}
