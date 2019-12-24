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

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "album")
    private String album;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "lyrics")
    private String lyrics;



    public Music(@NonNull int muID, @NonNull String title, @NonNull String artist, @NonNull String album,@NonNull String url,@NonNull String lyrics) {
        this.muID = muID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.url = url;
        this.lyrics = lyrics;
    }

    public int getMuID(){
        return this.muID;
    }
    public String getTitle(){
        return this.title;
    }
    public String getArtist(){
        return this.artist;
    }
    public String getAlbum(){
        return this.album;
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
        return ("MuID: " + muID + "\n" + "Title: " + title + "\n" +  "Artist: " + artist + "\n" + "Album: " + album + "\n" + "CoverURL: " + url + "\n" + "Lyrics: " + lyrics + "\n");
    }
}