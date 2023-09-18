package com.lgsa.bfademo;


import static com.lgsa.bfademo.SnapPicture.selectedImage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lgsa.bfademo.database.SummaryDBHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Summary extends AppCompatActivity{
    private static TextView tvDetail;
    private Button btSubmit;
    private ImageView backImage;
    private TextView tvBarrier;
    private SummaryDBHelper dbHelper;
    private ImageView photo;
    private byte[] imageBytes;

    @SuppressLint({"MissingInflatedId", "WrongThread", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        btSubmit=findViewById(R.id.btSubmit);
        //获取照片
        try {
            (photo=(ImageView)findViewById(R.id.summaryImg)).setImageBitmap((Bitmap)selectedImage);
            //将图片转化为字节数据，以便存入数据库
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((Bitmap) selectedImage).compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageBytes = stream.toByteArray();
        }catch (Exception e) {
            (photo = (ImageView) findViewById(R.id.summaryImg)).setImageURI((Uri) selectedImage);
            if (selectedImage == null) {
                photo=(ImageView) findViewById(R.drawable.nophoto);
            }else{
                try {
                    InputStream inputStream = getContentResolver().openInputStream((Uri) selectedImage);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    imageBytes = new byte[4096];
                    int n;
                    while ((n = inputStream.read(imageBytes)) > 0) {
                        outputStream.write(imageBytes, 0, n);
                    }
                    imageBytes = outputStream.toByteArray();
                    inputStream.close();
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        //获取收集到的位置csv格式
        SharedPreferences csvInfo = getSharedPreferences("csvInfo",MODE_PRIVATE);
        String location=csvInfo.getString("location.csv","");
        //获取细节描述
        tvDetail=findViewById(R.id.tvDetail);
        SharedPreferences detailInfo = getSharedPreferences("detailInfo", MODE_PRIVATE);
        tvDetail.setText(detailInfo.getString("des",""));
        //获取障碍物类型
        tvBarrier=findViewById(R.id.tvBarrier);
        SharedPreferences barrierInfo = getSharedPreferences("barrierInfo",MODE_PRIVATE);
        tvBarrier.setText(barrierInfo.getString("type",""));
        //获取障碍物位置
        SharedPreferences locationInfo = getSharedPreferences("locationInfo",MODE_PRIVATE);
        double latitude= Double.parseDouble(locationInfo.getString("lat",""));
        double longitude= Double.parseDouble(locationInfo.getString("lon",""));

        dbHelper=new SummaryDBHelper(this);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //把数据上传到UpLoad类中
                //获取提交数据收集时的时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                SharedPreferences.Editor summaryInfo = getSharedPreferences("summaryInfo", MODE_PRIVATE).edit();
                summaryInfo.putString("time",currentTime).apply();
                summaryInfo.putString("type",tvBarrier.getText().toString()).apply();
                summaryInfo.putString("location.csv",location).apply();
                summaryInfo.putString("des",tvDetail.getText().toString()).apply();


                SQLiteDatabase db2= dbHelper.getWritableDatabase();
                db2.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put("lat", latitude);
                    values.put("lon", longitude);
                    values.put("type", tvBarrier.getText().toString());
                    values.put("time",currentTime);
                    values.put("photo", imageBytes);
                    values.put("detail", tvDetail.getText().toString());
                    db2.insert("summaryInfo", null, values);
                    values.clear();   //clear后才可以插入第二份数据*/
                    db2.setTransactionSuccessful();
                }finally {
                    db2.endTransaction();
                }
                db2.close();

                Intent intent=new Intent(Summary.this,StopCollect.class);
                startActivity(intent);
                finish();
            }
        });

        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}