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

    @Query("UPDATE music_table SET music_id =:muID-1 WHERE music_id = :muID")
    void decrementMuID(int muID);

    @Query("SELECT * FROM music_table WHERE music_id = :muID")
    Music getItemById(int muID);

    @Query ("DELETE FROM music_table WHERE music_id=(SELECT MIN(music_id) FROM music_table)")
    void deleteFirstItem();

}

