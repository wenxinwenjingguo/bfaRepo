package com.lgsa.bfademo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.lgsa.bfademo.util.ViewUtil;


public class SignUp extends AppCompatActivity {
    private EditText etvUser;
    private EditText etvMi;
    private EditText etvRepeat;
    private Button btSignUp;
    private ImageView backImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        initEvents();

    }

    private void initView() {
        etvUser=findViewById(R.id.etvUser);
        etvMi=findViewById(R.id.etvMi);
        etvRepeat=findViewById(R.id.etvRepeat);
        btSignUp=findViewById(R.id.btSignUp);
        //给etvUser添加文本变更监听器
        etvUser.addTextChangedListener(new SignUp.HideTextWatcher(etvUser, 11));
        //给etvMi添加文本变更监听器
        etvMi.addTextChangedListener(new SignUp.HideTextWatcher(etvMi, 6));
        //给etvRepeat添加文本变更监听器
        etvRepeat.addTextChangedListener(new SignUp.HideTextWatcher(etvRepeat, 6));

        backImage=findViewById(R.id.backImage);


    }



    //定义一个编辑框监听器，在输入文本达到指定长度时自动隐藏输入法
    private class HideTextWatcher implements TextWatcher {
        private EditText mView;
        private int mMaxLength;

        public HideTextWatcher(EditText v, int maxLength) {
            this.mView = v;
            this.mMaxLength = maxLength;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        //在编辑框的输入文本变化后触发
        @Override
        public void afterTextChanged(Editable editable) {
            //获得已输入的文本字符串
            String str = editable.toString();
            //输入文本达到11位时，关闭输入法
            if (str.length() == mMaxLength) {
                //隐藏输入法键盘
                ViewUtil.hideOneInputMethod(SignUp.this, mView);
            }
        }
    }

    private void initEvents() {
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //注册完成后，返回登录界面
                toLog();
            }


        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //点击该按钮登录后，跳转到收集数据页面
    private void toLog() {
        String user=etvUser.getText().toString();
        String pwd =etvMi.getText().toString();
        if (user.length()<11){
            Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (user.length()<6) {
            Toast.makeText(this, "请输入6位密码", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if (!pwd.equals(etvRepeat.getText().toString())) {
                Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            //否则，提示用户注册成功
            else{
                SharedPreferences.Editor userInfo = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                userInfo.putString("user",etvUser.getText().toString());
                userInfo.putString("pwd",etvMi.getText().toString());
                userInfo.apply();
                //以下弹出提醒对话框，提示用户注册成功
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("注册成功");
                builder.setPositiveButton("确定返回", (dialogInterface, i) -> {
                    //结束当前活动页面

                    Intent intent=new Intent(this, LogIn.class);
                    intent.putExtra("user",user);
                    intent.putExtra("pwd",pwd);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("我再看看",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

}