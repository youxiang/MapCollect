package com.njscky.mapcollect.db;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class DbConfig {

    private final Context context;

    public DbConfig(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getDbFilesPath() {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = Environment.getExternalStorageDirectory();
        }

        return dir == null ? null : dir.getPath() + File.separator + context.getPackageName() + File.separator + "database";
    }
}
