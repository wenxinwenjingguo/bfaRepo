package com.lgsa.bfademo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lgsa.bfademo.util.ViewUtil;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private EditText etvUsername;
    private EditText etvPassword;
    private Button jumpGps;
    private Button jumpSignUp;
    private Button jumpFirst;
    private Button btForget;
    private ActivityResultLauncher<Intent> register;
    String mPassword=null;
    //private CheckBox ckRemember;  //记住密码功能还没有实现
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();
        initEvent();
    }
    private void initView() {
        etvUsername = findViewById(R.id.etvUsername);
        etvPassword = findViewById(R.id.etvPassword);
        jumpGps = findViewById(R.id.jumpGps);
        jumpSignUp = findViewById(R.id.jumpSignUp);
        jumpFirst = findViewById(R.id.jumpFirst);
        btForget = findViewById(R.id.btForget);
        //ckRemember=findViewById(R.id.ckRemember);
        backImage=findViewById(R.id.backImage);


        SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        etvUsername.setText(userInfo.getString("user",""));
        etvPassword.setText(userInfo.getString("pwd",""));

        //给etvUsername添加文本变更监听器
        etvUsername.addTextChangedListener(new LogIn.HideTextWatcher(etvUsername, 11));
        //给etvPassword添加文本变更监听器
        etvPassword.addTextChangedListener(new LogIn.HideTextWatcher(etvPassword, 6));

        //使用registerForActivityResult为获取到的结果注册结果回调，但其本身不会启动intent跳转
        //registerForActivityResult最后会返回一个ActivityResultLauncher对象用于启动intent跳转
        //registerForActivityResult第一个参数是ActivityResultContracts，除了通用的StartActivityForResult，还有TakePicture（拍照）、RequestPermission（请求单个权限）、PickContact（从通讯录获取联系人）等预定义好的contract
        //第二个参数中的onActivityResult() 方法用于处理获取到的数据结果
        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent=result.getData();
                if(intent !=null && result.getResultCode()== Activity.RESULT_OK){
                    //mPassword用于存储修改后的新密码
                    mPassword=intent.getStringExtra("newPassword");
                }
            }
        });

    }

    private void initEvent() {
        jumpSignUp.setOnClickListener(this);

        jumpFirst.setOnClickListener(this);

        btForget.setOnClickListener(this);

        jumpGps.setOnClickListener(this);

        backImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String account=etvUsername.getText().toString();
        switch(view.getId()){
            case R.id.jumpSignUp:  //注册按钮
                toSignup();
                break;

            case R.id.jumpGps:  //就是登录按钮
                toGps();
                break;

            case R.id.btForget:
                if (account.length()<11){
                    Toast.makeText(LogIn.this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    //跳转到找回密码页面
                    Intent intent = new Intent(this, FindPassword.class);
                    intent.putExtra ("etvUsername", account);
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.backImage:
                finish();
                break;

            case R.id.jumpFirst:
                toMainActivity();
                break;
        }
    }

    //跳转到注冊页面
    private void toSignup() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //则获取intent中的账号和密码  动态设置到EditText中
        String account=intent.getStringExtra("account");
        String password=intent.getStringExtra("password");
        etvUsername.setText(account);
        etvPassword.setText(password);
        mPassword=etvPassword.getText().toString();
    }
    //点击该按钮登录后，跳转到收集数据页面
    private void toGps() {
        if (etvUsername.length()<11){
            Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            return;
        }else {
            SharedPreferences userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);

            if (!userInfo.getString("pwd","").equals(etvPassword.getText().toString())) {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
            //否则，提示用户登录成功
            else{
                String desc=String.format("验证通过");
                //以下弹出提醒对话框，提示用户登录成功
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("登录成功");
                builder.setMessage(desc);
                builder.setPositiveButton("确定进入", (dialogInterface, i) -> {
                    //结束当前活动页面
                    Intent intent = new Intent(this, Gps.class);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("我再看看",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }

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
                ViewUtil.hideOneInputMethod(LogIn.this, mView);
            }
        }
    }

    //返回首页
    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}