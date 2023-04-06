package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context,0, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song,null); //TODO Need to check
        TextView textViewSongTitle = convertView.findViewById(R.id.textViewSongTitle);
        TextView textViewSongArtist = convertView.findViewById(R.id.textViewSongArtist);


        Song song = getItem(position);
        textViewSongTitle.setText(song.getTitle());
        textViewSongArtist.setText(song.getArtist());


        return convertView;
    }
}
