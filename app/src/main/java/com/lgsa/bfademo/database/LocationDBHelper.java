package com.lgsa.bfademo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "locationdata.db";
    public static final String TABLE_NAME = "locationInfo";
    private static final int DB_VERSION = 1;

    public LocationDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    //onCreate用来创建数据库，其内，执行建表语句
    //id作为主键
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " lat REAL NOT NULL," +
                " lon REAL NOT NULL);";
        db.execSQL(sql);
        //Toast.makeText(context, "成功建立数据库", Toast.LENGTH_SHORT).show();
    }
    //以上表示初始化操作完成

    //数据库版本更新 DB_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      /*  if (oldVersion < 2) {
            // 修改表结构
            db.execSQL("ALTER TABLE my_table ADD COLUMN age INTEGER");
        }*/
    }

}
