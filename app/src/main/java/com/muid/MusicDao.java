package com.muid;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import java.util.List;


@Dao
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Music music);

    @Query("DELETE FROM music_table")
    void deleteAll();

    @Query("SELECT * FROM music_table")
    List<Music> getAll();

    @Query("SELECT * FROM music_table WHERE music_id = :id")
    public Music getItemById(int id);

    @Delete
    void delete(Music music);
}

