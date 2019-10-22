package com.njscky.mapcollect.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njscky.mapcollect.db.CursorUtil;
import com.njscky.mapcollect.db.entitiy.Config;

import java.util.ArrayList;
import java.util.List;

public class ConfigDao extends BaseDao {

    public ConfigDao(SQLiteDatabase db) {
        super(db);
    }

    public List<Config> getConfigList() {
        final Cursor _cursor = mDb.rawQuery("select * from CONFIG", null);
        try {
            final int _cursorIndexOfGXTYPE = CursorUtil.getColumnIndexOrThrow(_cursor, "GXTYPE");
            final int _cursorIndexOfID = CursorUtil.getColumnIndexOrThrow(_cursor, "ID");
            final int _cursorIndexOfGX = CursorUtil.getColumnIndexOrThrow(_cursor, "GX");
            final int _cursorIndexOfGXAliases = CursorUtil.getColumnIndexOrThrow(_cursor, "GXAliases");
            final int _cursorIndexOfGD = CursorUtil.getColumnIndexOrThrow(_cursor, "GD");
            final int _cursorIndexOfGDAliases = CursorUtil.getColumnIndexOrThrow(_cursor, "GDAliases");
            final int _cursorIndexOfColorRGB = CursorUtil.getColumnIndexOrThrow(_cursor, "ColorRGB");
            final int _cursorIndexOfIMGTYPE = CursorUtil.getColumnIndexOrThrow(_cursor, "IMGTYPE");
            final int _cursorIndexOfISUSED = CursorUtil.getColumnIndexOrThrow(_cursor, "ISUSED");
            final int _cursorIndexOfTYPE = CursorUtil.getColumnIndexOrThrow(_cursor, "TYPE");
            final int _cursorIndexOfGLDM = CursorUtil.getColumnIndexOrThrow(_cursor, "GLDM");
            final List<Config> _result = new ArrayList<>(_cursor.getCount());
            while (_cursor.moveToNext()) {
                final Config _item;
                _item = new Config();
                _item.GXTYPE = _cursor.getString(_cursorIndexOfGXTYPE);
                _item.ID = _cursor.getLong(_cursorIndexOfID);
                _item.GX = _cursor.getString(_cursorIndexOfGX);
                _item.GXAliases = _cursor.getString(_cursorIndexOfGXAliases);
                _item.GD = _cursor.getString(_cursorIndexOfGD);
                _item.GDAliases = _cursor.getString(_cursorIndexOfGDAliases);
                _item.ColorRGB = _cursor.getString(_cursorIndexOfColorRGB);
                _item.IMGTYPE = _cursor.getString(_cursorIndexOfIMGTYPE);
                _item.ISUSED = _cursor.getInt(_cursorIndexOfISUSED);
                _item.TYPE = _cursor.getString(_cursorIndexOfTYPE);
                _item.GLDM = _cursor.getString(_cursorIndexOfGLDM);
                _result.add(_item);
            }
            return _result;
        } finally {
            _cursor.close();
        }
    }
}
