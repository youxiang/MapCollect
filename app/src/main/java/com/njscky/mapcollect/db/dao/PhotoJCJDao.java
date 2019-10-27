package com.njscky.mapcollect.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.util.CursorUtil;

import com.njscky.mapcollect.db.entitiy.PhotoJCJ;

import java.util.ArrayList;
import java.util.List;

public class PhotoJCJDao extends BaseDao {

    public PhotoJCJDao(SQLiteDatabase db) {
        super(db);
    }

    public List<PhotoJCJ> getPhoto(String JCJBH, String ZPLX) {
        final Cursor _cursor = mDb.query(
                "ZP_JCJ",
                null,
                "JCJBH = ? and ZPLX = ?",
                new String[]{JCJBH, ZPLX},
                null,
                null,
                null
        );

        try {
            final int _cursorIndexOfJCJBH = CursorUtil.getColumnIndexOrThrow(_cursor, "JCJBH");
            final int _cursorIndexOfZPBH = CursorUtil.getColumnIndexOrThrow(_cursor, "ZPBH");
            final int _cursorIndexOfZPLX = CursorUtil.getColumnIndexOrThrow(_cursor, "ZPLX");
            final int _cursorIndexOfZPLJ = CursorUtil.getColumnIndexOrThrow(_cursor, "ZPLJ");
            final int _cursorIndexOfBZ = CursorUtil.getColumnIndexOrThrow(_cursor, "BZ");
            final List<PhotoJCJ> _result = new ArrayList<PhotoJCJ>(_cursor.getCount());
            while (_cursor.moveToNext()) {
                final PhotoJCJ _item;
                _item = new PhotoJCJ();
                _item.JCJBH = _cursor.getString(_cursorIndexOfJCJBH);
                _item.ZPBH = _cursor.getString(_cursorIndexOfZPBH);
                _item.ZPLX = _cursor.getString(_cursorIndexOfZPLX);
                _item.ZPLJ = _cursor.getString(_cursorIndexOfZPLJ);
                _item.BZ = _cursor.getString(_cursorIndexOfBZ);
                _result.add(_item);
            }
            return _result;
        } finally {
            _cursor.close();
        }
    }

}
