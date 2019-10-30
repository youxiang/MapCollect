package com.njscky.mapcollect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.njscky.mapcollect.db.entitiy.DaoMaster;
import com.njscky.mapcollect.db.entitiy.DaoSession;
import com.njscky.mapcollect.util.AppExecutors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        String dbFilesPath = dbConfig.getDbFilesPath();
        File dbDir = new File(dbFilesPath);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
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

    public String getDbFilesPath() {
        return dbConfig.getDbFilesPath();
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


    public void importDb(String path, ImportDbCallback callback) {
        AppExecutors.DB.execute(() -> {
            File dbDir = new File(dbConfig.getDbFilesPath());
            File inputDbFile = new File(path);
            File outputDbFile = new File(dbDir, inputDbFile.getName());

            long total = inputDbFile.length();

            OutputStream os = null;
            InputStream is = null;
            long imported = 0L;

            try {
                if (!outputDbFile.exists()) {
                    outputDbFile.createNewFile();
                }
                os = new FileOutputStream(outputDbFile);
                is = new FileInputStream(inputDbFile);
                int length = -1;
                byte[] buffer = new byte[1024 * 8];
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                    imported += length;

                    if (callback != null) {
                        long finalImported = imported;
                        AppExecutors.MAIN.execute(() -> {
                            callback.onImporting(finalImported, total);
                        });
                    }
                }

                if (callback != null) {
                    AppExecutors.MAIN.execute(() -> {
                        callback.onImportSuccess(outputDbFile.getAbsolutePath());
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();

                if (callback != null) {
                    AppExecutors.MAIN.execute(() -> {
                        callback.onImportError(e);
                    });
                }
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public interface ImportDbCallback {
        void onImportSuccess(String outputDbFilePath);

        void onImportError(Exception e);

        void onImporting(long imported, long total);
    }
}
