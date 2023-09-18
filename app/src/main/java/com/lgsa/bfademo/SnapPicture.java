package com.lgsa.bfademo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class SnapPicture extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int THUMBNAIL = 2;
    private ImageView ivPhoto;
    private Button btWithoutPic;
    private Button but1;
    private Button but2;
    private ImageView backImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_picture);
        ivPhoto=findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(R.drawable.photo);
        btWithoutPic = findViewById(R.id.btWithoutPic);
        but1=findViewById(R.id.but1);
        but2=findViewById(R.id.but2);
        backImage=findViewById(R.id.backImage);

        btWithoutPic.setOnClickListener(this);
        but1.setOnClickListener(this);
        but2.setOnClickListener(this);
        backImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but1:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;
            case R.id.but2:
                Intent xj = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(xj, THUMBNAIL);
                break;
            case R.id.btWithoutPic:
                Intent intent2 = new Intent(this, Summary.class);
                startActivity(intent2);
//              finish();
                break;
            case R.id.backImage:
                finish();
                break;
        }

    }

    public static Object selectedImage ;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            //(ImageView) findViewById(R.id.ivPhoto))
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            ivPhoto.setImageURI((Uri) (selectedImage));

        }
        if (requestCode == THUMBNAIL && resultCode == RESULT_OK && null != data) {

            Bundle e = data.getExtras();
            selectedImage = (Bitmap)e.get("data");
            ivPhoto.setImageBitmap((Bitmap)(selectedImage));
        }

    }

   private Uri getDestinationUri() {
        String fileName = String.format("fr_crop_%s.jpg", System.currentTimeMillis());
        File cropFile = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        return Uri.fromFile(cropFile);
    }

}