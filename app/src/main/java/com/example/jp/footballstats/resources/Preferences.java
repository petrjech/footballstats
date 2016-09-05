package com.example.jp.footballstats.resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Preferences {

    public static final String PREFS_NAME = "FootballStatsPrefs";
    public static final String IS_AUTOMATIC_BACKUP_ON = "isAutomaticBackupOn";
    public static final String BACKUP_PATH = "backupPath";
    public static final int BACKUP_RETRY_TIMES = 3;
    public static final long BACKUP_RETRY_TIME = 3000;
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final int BACKUP_FREQUENCY_IN_DAYS = 1;
    public static final String INIT_DATABASE_RESTORE = "initDatabaseRestore";

    public static String getFormatedDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.US).format(date);
    }

    public static String getFormatedDateTime(Date date) {
        return new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US).format(date);
    }
}
