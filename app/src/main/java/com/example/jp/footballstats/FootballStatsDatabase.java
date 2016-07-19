package com.example.jp.footballstats;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class FootballStatsDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "footballstats.db";
    private static final String DATABASE_BACKUP_NAME = "footballstats.bak";
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

    protected static String checkLastBackup() {
        String result = "";
        //TODO implement backup check
        return result;
    }

    protected static boolean backupDatabase(Context context){
        boolean isResultOK = true;

        File sd = Environment.getExternalStorageDirectory();
        if (sd == null || !sd.canWrite()) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_backup_no_sd), Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        String sep = File.separator;
        String databasePath = sep + sep + "data" + sep + sep + "data" + sep + sep + context.getPackageName() + sep + sep + "databases" + sep + sep;

        File databaseFile = new File(databasePath, DATABASE_NAME);
        File backupFile = new File(sd, DATABASE_BACKUP_NAME);

        if (!databaseFile.canRead()) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_no_database), Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        FileChannel src = null;
        FileChannel dst = null;
        try {
            src = new FileInputStream(databaseFile).getChannel();
            dst = new FileOutputStream(backupFile).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_open_backup_file), Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
            isResultOK = false;
        } catch (IOException e) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_write_backup), Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
            isResultOK =  false;
        } finally {
            try
            {
                if (src != null) src.close();
                if (dst != null) dst.close();
            }
            catch(IOException ignored) {}

        }
        return isResultOK;
    }
}
