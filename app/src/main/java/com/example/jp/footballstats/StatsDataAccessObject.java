package com.example.jp.footballstats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class StatsDataAccessObject {

    static final String DATE_FORMAT = "yyyyMMdd";

    private static final String SEARCH_LIMIT = "20";

    private static StatsDataAccessObject instance = null;

    private SQLiteDatabase database;
    private FootballStatsDatabase dbHelper;

    private StatsDataAccessObject(Context context) {
        dbHelper = new FootballStatsDatabase(context);
    }

    static StatsDataAccessObject getInstance(Context context) {
        if (instance == null) {
            instance = new StatsDataAccessObject(context);
        }
        return instance;
    }

    static String createTablePlayers() {
        return "CREATE TABLE PLAYERS("
                + "_ID INTEGER PRIMARY KEY ASC,"
                + "PLAYER TEXT NOT NULL"
                + ");";
    }

    static String createTableGames() {
        return "CREATE TABLE GAMES("
                + "_ID INTEGER PRIMARY KEY ASC,"
                + "PLAYERID INTEGER NOT NULL,"
                + "DATE TEXT,"
                + "RESULT INTEGER,"
                + "ELO INTEGER,"
                + "NOTE TEXT"
                + ");";
    }

    void openDB() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    void openReadOnlyDB() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    void closeDB() {
        dbHelper.close();
    }

    void searchPlayer(String search, ArrayList<Player> results) {

        results.clear();

        this.openReadOnlyDB();
        Cursor cursor = database.rawQuery("select * from players where player like ? order by player collate nocase limit ?", new String[]{search + "%", SEARCH_LIMIT});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Player player = cursorToPlayer(cursor);
            results.add(player);
            cursor.moveToNext();
        }

        cursor.close();
        this.closeDB();
    }

    void searchGames(long playerID, ArrayList<Game> gameArrayList) {

        gameArrayList.clear();

        this.openReadOnlyDB();
        Cursor cursor = database.rawQuery("select * from games where playerid = ? order by date desc limit ?", new String[]{String.valueOf(playerID), SEARCH_LIMIT});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game game = cursorToGame(cursor);
            gameArrayList.add(game);
            cursor.moveToNext();
        }

        cursor.close();
        this.closeDB();
    }

    private Player cursorToPlayer(Cursor cursor) {
        return new Player(cursor.getLong(0), cursor.getString(1));
    }

    private Game cursorToGame(Cursor cursor) {
        Game game = new Game(cursor.getLong(0));
        game.setPlayerID(cursor.getLong(1));
        game.setDate(cursor.getString(2));
        game.setResult(cursor.getInt(3));
        game.setElo(cursor.getInt(4));
        game.setNote(cursor.getString(5));
        return game;
    }

    Player createPlayer(String player) {
        ContentValues values = new ContentValues();
        values.put("player", player);

        this.openDB();
        long insertId = database.insert("players", null, values);
        this.closeDB();

        return new Player(insertId, player);
    }

    // Case sensitive
    boolean containsPlayer(String name) {
        this.openReadOnlyDB();
        Cursor cursor = database.rawQuery("select * from players where player = ? collate binary limit 1", new String[]{name});
        boolean result = cursor.getCount() == 1;
        cursor.close();
        this.closeDB();
        return result;
    }

    void saveGame(Game game) {
        ContentValues values = new ContentValues();
        values.put("playerid", game.getPlayerID());
        values.put("date", game.getDate());
        values.put("result", game.getResult());
        values.put("elo", game.getElo());
        values.put("note", game.getNote());

        this.openDB();
        database.insert("games", null, values);
        this.closeDB();
    }

    void updateGame(Game game) {
        ContentValues values = new ContentValues();
        values.put("playerid", game.getPlayerID());
        values.put("date", game.getDate());
        values.put("result", game.getResult());
        values.put("elo", game.getElo());
        values.put("note", game.getNote());

        this.openDB();
        database.update("games", values, "rowid = ?", new String[]{"" + game.getGameID()});
        this.closeDB();
    }

    void deletePlayer(long playerID) {
        this.openDB();
        database.delete("games", "playerid = ?", new String[]{"" + playerID});
        database.delete("players", "_id = ?", new String[]{"" + playerID});
        this.closeDB();
    }

    void deleteGame(Game game) {
        this.openDB();
        database.delete("games", "rowid = ?", new String[]{"" + game.getGameID()});
        this.closeDB();
    }

    void getGameResults(ArrayList<Integer> wins, ArrayList<Integer> draws, ArrayList<Integer> loses) {
        int result;
        this.openReadOnlyDB();
        Cursor cursor = database.rawQuery("select elo, result from games", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursor.getInt(1);
            switch (result) {
                case 0:
                    loses.add(cursor.getInt(0));
                    break;
                case 1:
                    draws.add(cursor.getInt(0));
                    break;
                case 2:
                    wins.add(cursor.getInt(0));
                    break;
            }
            cursor.moveToNext();
        }
        this.closeDB();
    }
}