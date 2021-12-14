package com.paradiso.movie.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.paradiso.movie.model.PreDataMovies;

import java.util.ArrayList;

public class DatabaseHelperMovie extends SQLiteOpenHelper {
    private  static final String TAG = "DatabaseHelper";

    private  static final String TABLE_NAME = "movies";
    private  static final String ID = "ID";
    private  static final String title = "title";
    private  static final String moviePoster = "moviePoster";
    private  static final String year = "year";
    private  static final String director = "director";



    public DatabaseHelperMovie(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +ID+ " TEXT, " + title+ " TEXT," + year+ " TEXT," + director+ " TEXT, " +moviePoster+ " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean addData(PreDataMovies preDataMovies){
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE ID = '" + preDataMovies.getId() + "'");
//        String queryString = "SELECT * FROM " + TABLE_NAME + " where " + ID + " =" + preDataMovies.getId();

//        Cursor cursor = db1.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE ID = '" + preDataMovies.getId() + "'", null);
//        Log.i(TAG, "addData: "+cursor.getCount());
//        if (cursor.getCount() > 0){
//            Log.i(TAG, "addData: "+cursor.getCount());
//            db1.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE ID = '" + preDataMovies.getId() + "'");
//        }
////        deleteTitle(db,preDataMovies.getId());
        SQLiteDatabase db = this.getWritableDatabase();




        ContentValues cv = new ContentValues();
        cv.put(ID,preDataMovies.getId());
        cv.put(title,preDataMovies.getTitle());
        cv.put(moviePoster,preDataMovies.getPoster());

        cv.put(year,preDataMovies.getYear());

        long insert = db.insert(TABLE_NAME, null, cv);
        if(insert == -1){
           return false;
        }else{
            return true;
        }
    }

    public ArrayList<PreDataMovies> getMovies(){
        ArrayList<PreDataMovies> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.moveToLast()){
            do{
               String movieID = cursor.getString(0);
                String title = cursor.getString(1);
                String year = cursor.getString(2);
                String director = cursor.getString(3);
                String posterUrl = cursor.getString(4);

                PreDataMovies preDataMovies = new PreDataMovies(movieID,posterUrl,title,year);
                returnList.add(preDataMovies);

            }while (cursor.moveToPrevious());

        }else{

        }
        cursor.close();
        db.close();


        return returnList;

    }
    public boolean deleteTitle(SQLiteDatabase db,String name)
    {
        return db.delete(TABLE_NAME, ID + "=" + name, null) > 0;
    }
}
