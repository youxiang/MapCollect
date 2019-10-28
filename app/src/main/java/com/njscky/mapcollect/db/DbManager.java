package com.njscky.mapcollect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.njscky.mapcollect.db.entitiy.DaoMaster;
import com.njscky.mapcollect.db.entitiy.DaoSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

    private static final String TAG = DbManager.class.getSimpleName();

    private volatile static DbManager instance;

    DbConfig dbConfig;

    private SQLiteDatabase database;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private String dbPath;
    private Context context;

    private DbManager(Context context) {
        this.context = context.getApplicationContext();
        if (dbConfig == null) {
            dbConfig = new DbConfig(context);
        }
    }

    public static DbManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager(context);
                }
            }
        }
        return instance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * Open database
     *
     * @return
     */
    public boolean open(String path) {
        Log.i(TAG, "open: " + path);
        if (!isDatabaseClosed()) {
            if (TextUtils.equals(path, this.dbPath)) {
                Log.i(TAG, "open: database was open");
                return true;
            }
            close();
        }

        database = SQLiteDatabase.openOrCreateDatabase(path, null);
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();

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
            daoMaster = null;
            daoSession = null;
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

    public List<File> getDbFiles() {
        List<File> rst = new ArrayList<>();

        String dbDirPath = dbConfig.getDbFilesPath();
        File dir = new File(dbDirPath);

        if (!dir.exists() || !dir.isDirectory()) {
            return rst;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return rst;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".db")) {
                rst.add(file);
            }
        }

        return rst;

    }


}
