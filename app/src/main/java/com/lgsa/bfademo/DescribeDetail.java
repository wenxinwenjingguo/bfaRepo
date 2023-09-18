package com.lgsa.bfademo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class DescribeDetail extends AppCompatActivity implements View.OnClickListener {
    private Button btNext;
    private EditText etvDescribe;
    private ImageView backImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe_detail);
        btNext= findViewById(R.id.btNext);
        etvDescribe=findViewById(R.id.etvDescribe);
        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
        btNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btNext:
                SharedPreferences.Editor detailInfo = getSharedPreferences("detailInfo", MODE_PRIVATE).edit();
                String description=etvDescribe.getText().toString();
                detailInfo.putString("des",description).apply();

                Intent intent1 = new Intent(this,SnapPicture.class);
                //intent.putExtra("etvDescribe", (CharSequence) etvDescribe);
                startActivity(intent1);
                finish();
                break;
            case R.id.backImage:
                finish();
                break;
        }

    }
}