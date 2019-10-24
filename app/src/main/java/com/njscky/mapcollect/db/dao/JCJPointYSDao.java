package com.njscky.mapcollect.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njscky.mapcollect.db.CursorUtil;
import com.njscky.mapcollect.db.entitiy.JCJPointYS;

import java.util.ArrayList;
import java.util.List;

public class JCJPointYSDao extends BaseDao {

    public JCJPointYSDao(SQLiteDatabase db) {
        super(db);
    }

    public JCJPointYS queryPointByBH(String JCJBH) {
        Cursor _cursor = mDb.rawQuery("select * from YS_POINT_JCJ where JCJBH = '" + JCJBH + "'", null);

        try {
            if (_cursor.moveToFirst()) {
                final int _cursorIndexOfJCJBH = CursorUtil.getColumnIndexOrThrow(_cursor, "JCJBH");
                final int _cursorIndexOfJGCZ = CursorUtil.getColumnIndexOrThrow(_cursor, "JGCZ");
                final int _cursorIndexOfJGQK = CursorUtil.getColumnIndexOrThrow(_cursor, "JGQK");
                final int _cursorIndexOfJSQK = CursorUtil.getColumnIndexOrThrow(_cursor, "JSQK");
                final int _cursorIndexOfJSCZ = CursorUtil.getColumnIndexOrThrow(_cursor, "JSCZ");
                final int _cursorIndexOfJSCC = CursorUtil.getColumnIndexOrThrow(_cursor, "JSCC");
                final int _cursorIndexOfFSWLX = CursorUtil.getColumnIndexOrThrow(_cursor, "FSWLX");
                final int _cursorIndexOfSZDL = CursorUtil.getColumnIndexOrThrow(_cursor, "SZDL");
                final int _cursorIndexOfSFXG = CursorUtil.getColumnIndexOrThrow(_cursor, "SFXG");
                final int _cursorIndexOfBZ = CursorUtil.getColumnIndexOrThrow(_cursor, "BZ");
                final int _cursorIndexOfXZB = CursorUtil.getColumnIndexOrThrow(_cursor, "XZB");
                final int _cursorIndexOfYZB = CursorUtil.getColumnIndexOrThrow(_cursor, "YZB");

                final JCJPointYS _item;
                _item = new JCJPointYS();
                _item.JCJBH = _cursor.getString(_cursorIndexOfJCJBH);
                _item.JGCZ = _cursor.getString(_cursorIndexOfJGCZ);
                _item.JGQK = _cursor.getString(_cursorIndexOfJGQK);
                _item.JSQK = _cursor.getString(_cursorIndexOfJSQK);
                _item.JSCZ = _cursor.getString(_cursorIndexOfJSCZ);
                _item.JSCC = _cursor.getString(_cursorIndexOfJSCC);
                _item.FSWLX = _cursor.getString(_cursorIndexOfFSWLX);
                _item.SZDL = _cursor.getString(_cursorIndexOfSZDL);
                _item.SFXG = _cursor.getString(_cursorIndexOfSFXG);
                _item.BZ = _cursor.getString(_cursorIndexOfBZ);
                _item.XZB = _cursor.getFloat(_cursorIndexOfXZB);
                _item.YZB = _cursor.getFloat(_cursorIndexOfYZB);

                return _item;
            }
            return null;
        } finally {
            _cursor.close();
        }
    }

    public List<JCJPointYS> queryPoints(int pageIndex, int pageSize) {
        List<JCJPointYS> rst = new ArrayList<>();
        mDb.beginTransaction();

        long total = 0;

        Cursor _cursor = null;
        try {
            _cursor = mDb.rawQuery("select count(*) from YS_POINT_JCJ", null);
            if (_cursor.moveToFirst()) {
                total = _cursor.getLong(0);
            }

            if (total <= 0) {
                return rst;
            }

            if (getOffset(pageIndex, pageSize) >= total) {
                return rst;
            }

            _cursor = mDb.rawQuery("select * from YS_POINT_JCJ limit " + pageSize + " offset " + getOffset(pageIndex, pageSize), null);

            final int _cursorIndexOfJCJBH = CursorUtil.getColumnIndexOrThrow(_cursor, "JCJBH");
            final int _cursorIndexOfJGCZ = CursorUtil.getColumnIndexOrThrow(_cursor, "JGCZ");
            final int _cursorIndexOfJGQK = CursorUtil.getColumnIndexOrThrow(_cursor, "JGQK");
            final int _cursorIndexOfJSQK = CursorUtil.getColumnIndexOrThrow(_cursor, "JSQK");
            final int _cursorIndexOfJSCZ = CursorUtil.getColumnIndexOrThrow(_cursor, "JSCZ");
            final int _cursorIndexOfJSCC = CursorUtil.getColumnIndexOrThrow(_cursor, "JSCC");
            final int _cursorIndexOfFSWLX = CursorUtil.getColumnIndexOrThrow(_cursor, "FSWLX");
            final int _cursorIndexOfSZDL = CursorUtil.getColumnIndexOrThrow(_cursor, "SZDL");
            final int _cursorIndexOfSFXG = CursorUtil.getColumnIndexOrThrow(_cursor, "SFXG");
            final int _cursorIndexOfBZ = CursorUtil.getColumnIndexOrThrow(_cursor, "BZ");
            final int _cursorIndexOfXZB = CursorUtil.getColumnIndexOrThrow(_cursor, "XZB");
            final int _cursorIndexOfYZB = CursorUtil.getColumnIndexOrThrow(_cursor, "YZB");
            while (_cursor.moveToNext()) {
                final JCJPointYS _item;
                _item = new JCJPointYS();
                _item.JCJBH = _cursor.getString(_cursorIndexOfJCJBH);
                _item.JGCZ = _cursor.getString(_cursorIndexOfJGCZ);
                _item.JGQK = _cursor.getString(_cursorIndexOfJGQK);
                _item.JSQK = _cursor.getString(_cursorIndexOfJSQK);
                _item.JSCZ = _cursor.getString(_cursorIndexOfJSCZ);
                _item.JSCC = _cursor.getString(_cursorIndexOfJSCC);
                _item.FSWLX = _cursor.getString(_cursorIndexOfFSWLX);
                _item.SZDL = _cursor.getString(_cursorIndexOfSZDL);
                _item.SFXG = _cursor.getString(_cursorIndexOfSFXG);
                _item.BZ = _cursor.getString(_cursorIndexOfBZ);
                _item.XZB = _cursor.getFloat(_cursorIndexOfXZB);
                _item.YZB = _cursor.getFloat(_cursorIndexOfYZB);
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
