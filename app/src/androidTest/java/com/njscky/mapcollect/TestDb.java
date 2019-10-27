package com.njscky.mapcollect;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.njscky.mapcollect.db.RoomContext;
import com.njscky.mapcollect.db.entitiy.PhotoJCJ;

@Database(entities = {PhotoJCJ.class}, version = 1, exportSchema = false)
public abstract class TestDb extends RoomDatabase {

    private static final String DB_NAME = "MapCollect.db";

    public static TestDb createDatabase(Context ctx) {
        RoomContext context = new RoomContext(ctx);
        return Room.databaseBuilder(context, TestDb.class, DB_NAME)
                .build();
    }

}
