package com.fgapps.voicetest.Model;

/**
 * Created by (Engenharia) Felipe on 05/03/2018.
 */

public class Song {
    private long id;
    private String name;
    private String title;
    private String artist;
    private String folder;

    public Song(long songID, String songTitle, String songArtist, String songName, String folderName) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        name = songName;
        folder = folderName;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getName(){return name;}
    public String getFolder(){return folder;}
}
