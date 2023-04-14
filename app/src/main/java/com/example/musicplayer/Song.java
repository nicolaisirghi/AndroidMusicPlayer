package com.example.musicplayer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Song implements Serializable {
    private String title;
    private String artist;
    private String path;
    private boolean isFavorite;


    public Song(String title, String artist, String path,boolean isFavorite)  {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.isFavorite =isFavorite;

    }

    public String getTitle() {
        return this.title;
    }


    public String getArtist() {
        return this.artist;
    }


    public String getPath() {
        return this.path;
    }
    public boolean isFavorite(){
        return this.isFavorite;
    }
    public void setIsFavorite(boolean isFavorite)
    {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Song)) {
            return false;
        }

        Song otherSong = (Song) obj;

        return Objects.equals(title, otherSong.title)
                && Objects.equals(artist, otherSong.artist)
                && Objects.equals(path, otherSong.path);
    }

}


class TitleComparator implements Comparator<Song>
{
    @Override
    public int compare(Song song, Song t1) {
        return song.getTitle().compareTo(t1.getTitle());
    }
}
