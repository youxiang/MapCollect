package com.njscky.mapcollect.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.njscky.mapcollect.db.dao.BaseDao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DbManager {

    private static final String TAG = DbManager.class.getSimpleName();

    private volatile static DbManager instance;

    private SQLiteDatabase database;

    private String dbPath;

    private Map<Class<? extends BaseDao>, BaseDao> daoMap;

    private DbManager() {
        daoMap = new HashMap<>();
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


}
