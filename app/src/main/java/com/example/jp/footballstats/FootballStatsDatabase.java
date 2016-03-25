package com.example.jp.footballstats;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class FootballStatsDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "footballstats.db";
    private static final int DATABASE_VERSION = 1;

    FootballStatsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(StatsDataAccessObject.createTablePlayers());
        database.execSQL(StatsDataAccessObject.createTableGames());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FootballStatsDatabase.class.getName(), "Version 1 - upgrading of the database isn't implemented");
        throw new UnsupportedOperationException("Version 1 - upgrading of the database isn't implemented");
    }
}
