package com.njscky.mapcollect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.njscky.mapcollect.db.dao.BaseDao;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager {

    private static final String TAG = DbManager.class.getSimpleName();

    private volatile static DbManager instance;
    DbConfig dbConfig;
    private SQLiteDatabase database;
    private String dbPath;
    private Map<Class<? extends BaseDao>, BaseDao> daoMap;

    private Context context;

    private DbManager(Context context) {
        this.context = context.getApplicationContext();
        daoMap = new HashMap<>();
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
            daoMap.clear();
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

    public <T extends BaseDao> T getDao(Class<T> clazz) {
        if (database == null) {
            throw new RuntimeException("Cannot get DAO for " + clazz);
        }
        BaseDao dao = daoMap.get(clazz);

        if (dao == null) {
            try {
                Constructor<T> constructor = clazz.getConstructor(SQLiteDatabase.class);
                dao = constructor.newInstance(database);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (dao != null) {
                daoMap.put(clazz, dao);
            }

        }

        return (T) dao;

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
