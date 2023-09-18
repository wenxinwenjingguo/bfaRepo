package com.lgsa.bfademo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SummaryDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "summarydata.db";
    private static final String TABLE_NAME = "summaryInfo";
    private static final int DB_VERSION = 1;
    public SummaryDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db2) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " lat REAL NOT NULL," +
                " lon REAL NOT NULL," +
                " type TEXT NOT NULL," +
                " time TEXT NOT NULL," +
                " photo BLOB," +
                " detail TEXT);";
        db2.execSQL(sql);
        //Toast.makeText(context, "成功建立数据库", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*  if (oldVersion < 2) {
            // 修改表结构
            db.execSQL("ALTER TABLE my_table ADD COLUMN age INTEGER");
        }*/
    }
    public void deleteItem(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("summaryInfo", "time=?", new String[]{time});
        db.close();
    }
}
