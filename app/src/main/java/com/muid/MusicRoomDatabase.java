package com.muid;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Music.class}, version = 1)
public abstract class MusicRoomDatabase extends RoomDatabase {

    public abstract MusicDao getMusicDao();

    private static volatile MusicRoomDatabase INSTANCE;

    public static MusicRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (MusicRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MusicRoomDatabase.class, "music_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}


