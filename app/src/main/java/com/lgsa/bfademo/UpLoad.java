package com.lgsa.bfademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lgsa.bfademo.adapter.RecycleAdapter;
import com.lgsa.bfademo.database.SummaryDBHelper;
import com.lgsa.bfademo.entity.TextBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpLoad extends AppCompatActivity {
    private ImageView backImage;
    private static final String TAG = "mix";
    private RecyclerView recyclerView = null;
    private List<TextBean> lists=new ArrayList<>(); // 初始化数据集合
    private RecycleAdapter adapter = null;
    private SummaryDBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load);
        backImage = findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 获取组件
        recyclerView = findViewById(R.id.reList);
        adapter = new RecycleAdapter(lists);
        // 设置管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.getItemAnimator().setChangeDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);
        //在列表项之间绘制分割线
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        onPreviousPageFinished();
    }

    public void onPreviousPageFinished() {
        dbHelper=new SummaryDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("summaryInfo",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                @SuppressLint("Range") double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
                //把当前的位置信息存为csv文件
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "location.csv";
                String filePath = baseDir + File.separator + fileName;
                File file = new File(filePath);
                try {
                    FileWriter writer = new FileWriter(file, true);
                    writer.append(lat + "," + lon + "\n");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                // 创建带有唯一标识符的数据模型
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
                TextBean t1 = new TextBean(id,time,type,fileName);
                adapter.addItem(t1);//调用了RecycleAdapter的方法，因为前面已经完成了adapter的初始化，所以可以调用
                Log.i(TAG,"执行了信息的输入");
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
    }
}