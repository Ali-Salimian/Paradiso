package com.paradiso.movie.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.paradiso.movie.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private  static final String TAG = "DatabaseHelper";

    private  static final String TABLE_NAME = "user";
    private  static final String ID = "ID";
    private  static final String userName = "userName";
    private  static final String userImage = "userImage";
//    private  static final String date = "date";




    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +ID+ " TEXT, " + userName+ " TEXT, " +userImage+ " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean addData(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE ID = '" + user.getId() + "'");

        ContentValues cv = new ContentValues();
        cv.put(ID,user.getId());
        cv.put(userName,user.getName());
        cv.put(userImage,user.getImageUrl());
        long insert = db.insert(TABLE_NAME, null, cv);
        if(insert == -1){
           return false;
        }else{
            return true;
        }
    }
    public List<User> getUsers()  {
            SQLiteDatabase db = this.getReadableDatabase();

            List<User> returnList = new ArrayList<>();
            String queryString = "SELECT * FROM " + TABLE_NAME;


            Cursor cursor = db.rawQuery(queryString, null);
            if (cursor.moveToLast()) {
                do {
                    String userID = cursor.getString(0);
                    String userName = cursor.getString(1);
                    String userImage = cursor.getString(2);

                    User user = new User(userName, userID, userImage);
                    returnList.add(user);

                } while (cursor.moveToPrevious());

            } else {

            }
        cursor.close();
        db.close();


        return returnList;

    }
}
