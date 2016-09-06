package com.example.jp.footballstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.jp.footballstats.resources.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

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

    static Date checkLastBackup(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Preferences.PREFS_NAME, 0);
        String backupPath = settings.getString(Preferences.BACKUP_PATH, "");
        File backup = new File(backupPath, DATABASE_BACKUP_NAME);
        Date lastModDate = null;
        if (backup.exists()) {
            lastModDate = new Date(backup.lastModified());
        }
        if (lastModDate == null) return null;
        return lastModDate;
    }

    static boolean backupDatabase(Context context, Runnable thread) {
        boolean resultOK;
        int attempts = 1;

        while (true) {
            resultOK = backupDatabaseAttempt(context);

            if (resultOK) {
                break;
            }

            attempts++;
            if (attempts > Preferences.BACKUP_RETRY_TIMES) {
                break;
            }

            try {
                thread.wait(Preferences.BACKUP_RETRY_TIME);
            } catch(InterruptedException ignore) { }
        }
        return resultOK;
    }

    static void restoreDatabase(Context context) {
        File sd = Environment.getExternalStorageDirectory();

        String sep = File.separator;
        String databasePath = sep + sep + "data" + sep + sep + "data" + sep + sep + context.getPackageName() + sep + sep + "databases" + sep + sep;

        File databaseFile = new File(databasePath, DATABASE_NAME);
        File backupFile = new File(sd, DATABASE_BACKUP_NAME);

        FileChannel src = null;
        FileChannel dst = null;
        try {
            src = new FileInputStream(backupFile).getChannel();
            dst = new FileOutputStream(databaseFile).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_open_backup_file), Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_write_backup), Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        } finally {
            try
            {
                if (src != null) src.close();
                if (dst != null) dst.close();
            }
            catch(IOException ignored) {}
        }
    }

    private static boolean backupDatabaseAttempt(Context context){
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
        if (isResultOK) saveBackupPath(context, sd.toString());
        return isResultOK;
    }

    private static void saveBackupPath(Context context, String path) {
        SharedPreferences settings = context.getSharedPreferences(Preferences.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Preferences.BACKUP_PATH, path);
        editor.apply();
    }
}
