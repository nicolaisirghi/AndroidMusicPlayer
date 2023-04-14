package com.example.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "MusicPlayerAndroid.db";
    private static final String TABLE_NAME = "Favorites";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "song_title";
    private static final String COLUMN_ARTIST = "song_artist";
    private static final String COLUMN_PATH = "song_path";
    private static final String COLUMN_FAVORITE = "song_isFavorite";

    public DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ARTIST + " TEXT," +
                COLUMN_PATH + " TEXT," +
                COLUMN_FAVORITE + " BOOLEAN);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    void addSong(String tittle, String artist, String path, boolean favorite) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, tittle);
        cv.put(COLUMN_ARTIST, artist);
        cv.put(COLUMN_PATH, path);
        cv.put(COLUMN_FAVORITE, favorite);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    Cursor getSong(String path) {
        try {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_PATH + "='" + path+"'";
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;

            if (db != null) {
                cursor = db.rawQuery(query, null);

            }
            return cursor;
        } catch (Exception e) {
            Log.d("test", e.getMessage());
        }
        return null;
    }


    Integer deleteSong(String path)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME,"song_path = ?",new String[]{path});
        }catch (Exception e ){
            Log.d("test",e.getMessage());
        }
        return -1;
    }
}
