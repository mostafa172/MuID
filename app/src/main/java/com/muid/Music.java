package com.muid;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "music_table")
public class Music {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "music_id")
    private int muID = 0;

    @ColumnInfo(name = "result")
    private String result;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "lyrics")
    private String lyrics;



    public Music(@NonNull int muID, @NonNull String result,@NonNull String url,@NonNull String lyrics) {
        this.muID = muID;
        this.result = result;
        this.url = url;
        this.lyrics = lyrics;
    }

    public int getMuID(){
        return this.muID;
    }
    public String getResult(){
        return this.result;
    }
    public String getUrl(){
        return this.url;
    }
    public String getLyrics(){
        return this.lyrics;
    }


    public void setMuID(int muID) {
        this.muID = muID;
    }

    @Override
    public String toString(){
        return ("MuID: " + muID + "\n" + "Result: " + result + "\n" + "CoverURL: " + url + "\n" + "Lyrics: " + lyrics + "\n");
    }
}