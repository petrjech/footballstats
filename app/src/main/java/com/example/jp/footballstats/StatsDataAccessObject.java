package com.example.jp.footballstats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class StatsDataAccessObject {

    private static final String SEARCH_LIMIT = "20";

    private static StatsDataAccessObject instance = null;

    private SQLiteDatabase database;
    private FootballStatsDatabase dbHelper;

    private StatsDataAccessObject(Context context){
        dbHelper = new FootballStatsDatabase(context);
    }

    static StatsDataAccessObject getInstance(Context context){
        if (instance == null){
            instance = new StatsDataAccessObject(context);
        }
        return instance;
    }

    static String createTablePlayers(){
        return "CREATE TABLE PLAYERS("
                +  "_ID INTEGER PRIMARY KEY ASC,"
                +  "PLAYER TEXT NOT NULL"
                +  ");";
    }

    static String createTableGames(){
        return "CREATE TABLE GAMES("
                +  "_ID INTEGER PRIMARY KEY ASC,"
                +  "PLAYERID INTEGER NOT NULL,"
                +  "DATE TEXT,"
                +  "RESULT INTEGER,"
                +  "ELO INTEGER,"
                +  "NOTE TEXT"
                +  ");";
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
        Cursor cursor = database.rawQuery("select * from players where player like ? limit ?", new String[]{search + "%", SEARCH_LIMIT});

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
        //Todo search order by date
        Cursor cursor = database.rawQuery("select * from games where playerid like ? limit ?", new String[]{String.valueOf(playerID) + "%", SEARCH_LIMIT});

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
        Game game = new Game(cursor.getLong(1));
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

    boolean containsPlayer(String name){
        this.openReadOnlyDB();
        Cursor cursor = database.rawQuery("select * from players where player like ? limit ?", new String[]{name, SEARCH_LIMIT});
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
}
