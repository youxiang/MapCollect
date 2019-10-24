package com.njscky.mapcollect.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njscky.mapcollect.db.CursorUtil;
import com.njscky.mapcollect.db.entitiy.JCJLineYS;

import java.util.ArrayList;
import java.util.List;

public class JCJLineYSDao extends BaseDao {
    public JCJLineYSDao(SQLiteDatabase db) {
        super(db);
    }

    public List<JCJLineYS> queryLines(int pageIndex, int pageSize) {
        List<JCJLineYS> rst = new ArrayList<>();
        mDb.beginTransaction();

        long total = 0;

        Cursor _cursor = null;
        try {
            _cursor = mDb.rawQuery("select count(*) from YS_LINE_JCJ", null);
            if (_cursor.moveToFirst()) {
                total = _cursor.getLong(0);
            }

            if (total <= 0) {
                return rst;
            }

            if (getOffset(pageIndex, pageSize) >= total) {
                return rst;
            }

            _cursor = mDb.rawQuery("select * from YS_LINE_JCJ limit " + pageSize + " offset " + getOffset(pageIndex, pageSize), null);

            final int _cursorIndexOfJCJBH = CursorUtil.getColumnIndexOrThrow(_cursor, "JCJBH");
            final int _cursorIndexOfLJBH = CursorUtil.getColumnIndexOrThrow(_cursor, "LJBH");
            final int _cursorIndexOfQDMS = CursorUtil.getColumnIndexOrThrow(_cursor, "QDMS");
            final int _cursorIndexOfGJ = CursorUtil.getColumnIndexOrThrow(_cursor, "GJ");
            final int _cursorIndexOfCZ = CursorUtil.getColumnIndexOrThrow(_cursor, "CZ");
            final int _cursorIndexOfSFXG = CursorUtil.getColumnIndexOrThrow(_cursor, "SFXG");
            final int _cursorIndexOfSFDTYZ = CursorUtil.getColumnIndexOrThrow(_cursor, "SFDTYZ");
            final int _cursorIndexOfSFHJ = CursorUtil.getColumnIndexOrThrow(_cursor, "SFHJ");
            final int _cursorIndexOfHJLX = CursorUtil.getColumnIndexOrThrow(_cursor, "HJLX");
            final int _cursorIndexOfLX = CursorUtil.getColumnIndexOrThrow(_cursor, "LX");
            final int _cursorIndexOfBZ = CursorUtil.getColumnIndexOrThrow(_cursor, "BZ");
            final int _cursorIndexOfQDXZB = CursorUtil.getColumnIndexOrThrow(_cursor, "QDXZB");
            final int _cursorIndexOfQDYZB = CursorUtil.getColumnIndexOrThrow(_cursor, "QDYZB");
            final int _cursorIndexOfZDXZB = CursorUtil.getColumnIndexOrThrow(_cursor, "ZDXZB");
            final int _cursorIndexOfZDYZB = CursorUtil.getColumnIndexOrThrow(_cursor, "ZDYZB");
            while (_cursor.moveToNext()) {
                final JCJLineYS _item;
                _item = new JCJLineYS();
                _item.JCJBH = _cursor.getString(_cursorIndexOfJCJBH);
                _item.LJBH = _cursor.getString(_cursorIndexOfLJBH);
                _item.QDMS = _cursor.getFloat(_cursorIndexOfQDMS);
                _item.GJ = _cursor.getString(_cursorIndexOfGJ);
                _item.CZ = _cursor.getString(_cursorIndexOfCZ);
                _item.SFXG = _cursor.getString(_cursorIndexOfSFXG);
                _item.SFDTYZ = _cursor.getString(_cursorIndexOfSFDTYZ);
                _item.SFHJ = _cursor.getString(_cursorIndexOfSFHJ);
                _item.HJLX = _cursor.getString(_cursorIndexOfHJLX);
                _item.LX = _cursor.getString(_cursorIndexOfLX);
                _item.BZ = _cursor.getString(_cursorIndexOfBZ);
                _item.QDXZB = _cursor.getFloat(_cursorIndexOfQDXZB);
                _item.QDYZB = _cursor.getFloat(_cursorIndexOfQDYZB);
                _item.ZDXZB = _cursor.getFloat(_cursorIndexOfZDXZB);
                _item.ZDYZB = _cursor.getFloat(_cursorIndexOfZDYZB);
                rst.add(_item);
            }
            return rst;
        } finally {
            if (_cursor != null) {
                _cursor.close();
            }
            mDb.endTransaction();
        }
    }
}
