package com.njscky.mapcollect.db.dao;

import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao {

    protected SQLiteDatabase mDb;

    public BaseDao(SQLiteDatabase db) {
        mDb = db;
    }

    protected int getOffset(int pageIndex, int pageSize) {
        return pageSize * pageIndex;
    }
}
