package com.njscky.mapcollect.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.njscky.mapcollect.db.dao.ConfigDao;

public class DbManager {

    private static final String TAG = DbManager.class.getSimpleName();

    private volatile static DbManager instance;

    private SQLiteDatabase database;

    private String dbPath;

    private DbManager() {
    }

    public static DbManager getInstance() {
        if (instance == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager();
                }
            }
        }
        return instance;
    }

    /**
     * Open database
     *
     * @param path
     * @return
     */
    public boolean open(String path) {
        Log.i(TAG, "open: " + path);
        if (!isDatabaseClosed()) {
            if (TextUtils.equals(path, this.dbPath)) {
                Log.i(TAG, "open: database was open");
                return true;
            } else {
                Log.w(TAG, "open: cannot open, database: " + this.dbPath);
                return false;
            }
        }

        database = SQLiteDatabase.openOrCreateDatabase(path, null);
        this.dbPath = path;
        return true;
    }

    /**
     * close database
     */
    public void close() {
        if (database != null) {
            database.close();
            database = null;
            dbPath = null;
        }
    }

    /**
     * Is database closed
     *
     * @return
     */
    public boolean isDatabaseClosed() {
        return database == null;
    }

    public ConfigDao getConfigDao() {
        if (database != null) {
            return new ConfigDao(database);
        }
        throw new RuntimeException("Open database first");
    }


}
