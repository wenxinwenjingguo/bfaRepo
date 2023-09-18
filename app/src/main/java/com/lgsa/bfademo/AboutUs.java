package com.lgsa.bfademo;

import static com.lgsa.bfademo.R.id.backImage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AboutUs extends AppCompatActivity implements View.OnClickListener {
    private ImageView backImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        finish();
    }
}