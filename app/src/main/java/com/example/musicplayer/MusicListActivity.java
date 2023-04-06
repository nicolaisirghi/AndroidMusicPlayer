package com.example.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MusicListActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION = 99;
    ArrayList<Song> songList;
    ListView listViewSongs;
    SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        listViewSongs = findViewById(R.id.listViewSongs);
        songList = new ArrayList<Song>();
        songAdapter = new SongAdapter(this, songList);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION); //todo
            return;
        } else {
            getSongs();

        }

        listViewSongs.setAdapter(songAdapter);
        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songList.get(position);
                Intent openMusicPlayer = new Intent(MusicListActivity.this, MainActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("songs",songList);
                startActivity(openMusicPlayer);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs();
            }
        }
    }

    private void getSongs() {
        ContentResolver contentResolver = getContentResolver();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                String title = songCursor.getString(indexTitle);
                String artist = songCursor.getString(indexArtist);
                String path = songCursor.getString(indexData);


                    songList.add(new Song(title, artist, path));




            } while (songCursor.moveToNext());
        }
        Collections.sort(songList,new TitleComparator());
        songAdapter.notifyDataSetChanged();


    }

}