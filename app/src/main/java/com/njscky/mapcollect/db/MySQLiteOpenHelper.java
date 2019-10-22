package com.njscky.mapcollect.db;

import android.content.Context;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

public class MySQLiteOpenHelper implements SupportSQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context, String name, Callback callback) {

    }

    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public void setWriteAheadLoggingEnabled(boolean enabled) {

    }

    @Override
    public SupportSQLiteDatabase getWritableDatabase() {
        return null;
    }

    @Override
    public SupportSQLiteDatabase getReadableDatabase() {
        return null;
    }

    @Override
    public void close() {

    }
}
