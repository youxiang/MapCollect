package com.njscky.mapcollect.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class RoomContext extends ContextWrapper {
    DbConfig config;

    public RoomContext(Context base) {
        super(base);
        config = new DbConfig(this);
    }

    @Override
    public File getDatabasePath(String name) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(config.getDbFilesPath());
        buffer.append(File.separator);
        buffer.append(name);
        return new File(buffer.toString());
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }


}
