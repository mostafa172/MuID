package com.muid;

import java.io.Serializable;

public class Song implements Serializable {
    String title , artist , album ,coverURL, lyrics;

    public Song(String title, String artist, String album, String coverURL, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.coverURL = coverURL;
        this.lyrics = lyrics;
    }
    public void reset(){
        this.title = "";
        this.artist = "";
        this.album = "";
        this.coverURL = "not found";
        this.lyrics = "";
    }
}
