package com.lgsa.bfademo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class FindPassword extends AppCompatActivity implements View.OnClickListener {
    private ImageView backImage;
    private String phone;
    private Button btVerify;
    private String mVerify;
    private Button btConfirm;
    private EditText etvNew;
    private EditText etvConfirmNew;
    private EditText etvVerify;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        //从上一个页面获取需要修改的手机号码
        phone = getIntent().getStringExtra("etvUsername");
        btVerify=findViewById(R.id.btVerify);
        btConfirm=findViewById(R.id.btConfirm);
        etvNew=findViewById(R.id.etvNew);
        etvConfirmNew=findViewById(R.id.etvConfirmNew);
        etvVerify=findViewById(R.id.etvVerify);
        btVerify.setOnClickListener(this);
        btConfirm.setOnClickListener(this);

        backImage=findViewById(R.id.backImage);
        backImage.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        //view.getId()就是获取当前点击的view的id，这个id一般是你在xml布局文件设置的id
        switch (view.getId()){
            //点击了获取验证码按钮
            case R.id.btVerify:
                //生成六位随机数字的验证码
                mVerify=String.format("%06d",new Random().nextInt(999999));
                //以下弹出提醒对话框，提示用户记住六位验证码数字
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("请记住验证码");
                builder.setMessage("手机号"+phone+"，本次验证码是"+mVerify+",请输入验证码");
                builder.setPositiveButton("好的",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            //点击了确定按钮
            case R.id.btConfirm:
                String newPassword =etvNew.getText().toString();
                String confirmPassword=etvConfirmNew.getText().toString();
                if (newPassword.length()!=6){
                    Toast.makeText(this, "请输入六位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //这里不用写else if 因为以上如果不成立，直接return 这里不能写else if否则会出错
                if (!newPassword.equals(confirmPassword)){
                    Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mVerify.equals(etvVerify.getText().toString())){
                    Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //否则，密码修改成功

                SharedPreferences.Editor userInfo = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                userInfo.putString("pwd",newPassword).apply();

                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                //以下把修改好的密码返回给上一个页面
                Intent intent = new Intent();
                intent.putExtra("newPassword", newPassword);
                setResult(Activity.RESULT_OK, intent);
                finish();

                break;
                case R.id.backImage:
                    finish();
                    break;
        }
    }
}