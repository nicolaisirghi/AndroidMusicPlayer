package com.example.musicplayer;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textTimeSong, textDurationSong, textTitle, textArtist;

    String TAG = "nicolai";
    ImageView coverImage;
    SeekBar seekBarTime;
    ImageButton buttonPlayMusic, buttonNextSong, buttonPrevSong, savedSong;

    MediaPlayer musicPlayer;

    Song song;
    ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        song = (Song) getIntent().getSerializableExtra("song");
        songList = (ArrayList<Song>) getIntent().getSerializableExtra("songs");


        textTimeSong = findViewById(R.id.textTimeSong);
        textDurationSong = findViewById(R.id.textDurationSong);

        seekBarTime = findViewById(R.id.seekBarTime);

        buttonPlayMusic = findViewById(R.id.buttonPlayMusic);
        buttonNextSong = findViewById(R.id.buttonNextSong);
        buttonPrevSong = findViewById(R.id.buttonPrevSong);

        savedSong = findViewById(R.id.savedSong);

        textTitle = findViewById(R.id.textViewTitle);
        textArtist = findViewById(R.id.textViewArtist);


        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(song.getPath());
            byte[] albumCover = retriever.getEmbeddedPicture();
            Bitmap albumCoverBitmap = BitmapFactory.decodeByteArray(albumCover, 0, albumCover.length);
            coverImage = findViewById(R.id.coverImage);
            coverImage.setImageBitmap(albumCoverBitmap);
        } catch (Exception e) {
            Log.e("nicolai", "Exception");
        }


        textTitle.setText(song.getTitle());
        textArtist.setText(song.getArtist());
        buttonPlayMusic = findViewById(R.id.buttonPlayMusic);
        musicPlayer = new MediaPlayer();
        try {
            musicPlayer.setDataSource(song.getPath());
            musicPlayer.prepare();
        } catch (IOException e) {

        }

        int savedSongDrawable = song.isFavorite() ? R.drawable.black_heart : R.drawable.white_heart;

        savedSong.setBackgroundResource(savedSongDrawable);
        musicPlayer.start();

        musicPlayer.setLooping(true);
        musicPlayer.seekTo(0);
        musicPlayer.setVolume(0.5f, 0.5f);

        buttonPlayMusic.setOnClickListener(this);
        buttonPrevSong.setOnClickListener(this);
        buttonNextSong.setOnClickListener(this);
        savedSong.setOnClickListener(this);
        String duration = millisecondsToString(musicPlayer.getDuration());
        textDurationSong.setText(duration);
        seekBarTime.setMax(musicPlayer.getDuration());


        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // play next song
                mp.reset();
                try {
                    Utils info = new Utils(songList, song).getInfoPlayer();
                    String path = songList.get(info.nextSong).getPath();
                    Log.d(TAG, "onCompletion: " + path);

                    mp.setDataSource(path);
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isCustomChange) {
                if (isCustomChange) {
                    musicPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(() -> {
            while (musicPlayer != null) {
                if (musicPlayer.isPlaying()) {
                    try {
                        final double current = musicPlayer.getCurrentPosition();

                        final String elapsedTime = millisecondsToString((int) current);


                        runOnUiThread(() -> {
                            textTimeSong.setText(elapsedTime);
                            seekBarTime.setProgress((int) current);
                        });

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    public String millisecondsToString(int time) {

        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        String elapsedTime = seconds > 9 ? minutes + ":" + seconds : minutes + ":0" + seconds;
        return elapsedTime;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonPlayMusic) {
            if (musicPlayer.isPlaying()) {
                buttonPlayMusic.setBackgroundResource(R.drawable.play_icon);
                musicPlayer.pause();
            } else {
                musicPlayer.start();
                buttonPlayMusic.setBackgroundResource(R.drawable.pause_icon);
            }
        }
        if (view.getId() == R.id.savedSong) {

            try {
                DataBase db = new DataBase(MainActivity.this);
                Cursor cursorDB = db.getSong(song.getPath());
                Song songDB = null;
                while (cursorDB.moveToNext()) {
                    String title = cursorDB.getString(1);
                    String artist = cursorDB.getString(2);
                    String path = cursorDB.getString(3);
                    boolean favorite = cursorDB.getInt(4) != 0;
                    songDB.setTitle(title);
                    songDB.setArtist(artist);
                    songDB.setPath(path);
                    songDB.setIsFavorite(favorite);
                }
                if (songDB != null) {
                    if (songDB.isFavorite()) {
                        savedSong.setBackgroundResource(R.drawable.black_heart);
                        db.updateSong(songDB.getTitle(), songDB.getPath(), songDB.getArtist(), true);
                        song.setIsFavorite(true);

                    } else {
                        savedSong.setBackgroundResource(R.drawable.white_heart);
                        db.updateSong(songDB.getTitle(), songDB.getPath(), songDB.getArtist(), false);
                        song.setIsFavorite(false);

                    }

                } else {
                    db.addSong(song.getTitle(), song.getArtist(), song.getPath(), song.isFavorite());
                }
            }
            catch (Exception e)
            {
                Log.d("nicolai",e.getMessage());
            }

        }
        if (view.getId() == R.id.buttonNextSong) {
            try {
                Intent openMusicPlayer = new Intent(MainActivity.this, MainActivity.class);
                Utils info = new Utils(songList, song).getInfoPlayer();
                openMusicPlayer.putExtra("song", songList.get(info.nextSong));
                openMusicPlayer.putExtra("songs", songList);
                musicPlayer.stop();
                finish();
                startActivity(openMusicPlayer);
            } catch (Exception e) {
                Log.d("nicolai", e.getMessage());
            }

        }

        if (view.getId() == R.id.buttonPrevSong) {
            try {
                Intent openMusicPlayer = new Intent(MainActivity.this, MainActivity.class);
                Utils info = new Utils(songList, song).getInfoPlayer();
                openMusicPlayer.putExtra("song", songList.get(info.prevSong));
                openMusicPlayer.putExtra("songs", songList);
                musicPlayer.stop();
                finish();
                startActivity(openMusicPlayer);
            } catch (Exception e) {
                Log.d("nicolai", e.getMessage());
            }
        }
    }
}