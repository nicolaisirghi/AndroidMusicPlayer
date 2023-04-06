package com.example.musicplayer;

import java.util.ArrayList;

public class Utils {
    public ArrayList<Song> songList;
    public Song currentSong;
    public int nextSong;
    public int prevSong;

     public Utils(ArrayList<Song> songList,Song currentSong)
     {
         this.songList = songList;
         this.currentSong = currentSong;
     }

    public Utils(ArrayList<Song> songList, Song currentSong, int nextSong, int prevSong) {
        this.songList = songList;
        this.currentSong = currentSong;
        this.nextSong = nextSong;
        this.prevSong = prevSong;
    }

    public Utils getInfoPlayer()
     {
         int position = songList.indexOf(currentSong);
         int prevSong = (position <= 1) ? songList.size() - 1 : position-1;
         int nextSong = (position >= songList.size() - 1) ? 0 : position+1;

         return new Utils(songList,currentSong,nextSong,prevSong);
     }
}
