package com.nuc.camphome.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nuc.camphome.R;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText phone_et;
    private EditText password_et;
    private Button login_btn;
    private LinearLayout loginloyout;
    private String phone;
    private String password;
    private String getValidateUserURL;
    private String times, code;
    private long applicationid = 1;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone_et = (EditText) findViewById(R.id.id_phonel_edit);
        password_et = (EditText) findViewById(R.id.id_password_edit);
        login_btn = (Button) findViewById(R.id.id_login_btn);
        loginloyout = (LinearLayout) findViewById(R.id.login_layout);

        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor=pref.edit();
        applicationid = pref.getLong("applicationID", 1);
        times= GetTimesAndCode.getTimes();
        code=GetTimesAndCode.getCode(times);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phone_et.getText().toString();
                password = password_et.getText().toString();
                login_btn.setEnabled(false);
                login_btn.setText("正在登陆...");
                if (phone_et.getText().toString().equals("") || password_et.getText().toString().equals("")) {
                    Snackbar.make(loginloyout, "账号或密码为空", Snackbar.LENGTH_SHORT).show();
                } else {
                    getValidateUserURL = Urls.ValidateUserURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&username=" +phone + "&password=" +password;
                    OkHttpUtils.ResultCallback<String> loginCallback = new OkHttpUtils.ResultCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            String r= response.substring(1,2);
                            String username=response.substring(2,response.length()-1);
                            if (r.equals("0")) {
                                editor.putString("username",username);
                                editor.commit();
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);

                                finish();

                            } else {
                                Snackbar.make(loginloyout, response, Snackbar.LENGTH_SHORT).show();
                                login_btn.setText("登陆");
                                login_btn.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.printStackTrace();
                            login_btn.setText("登陆");
                            login_btn.setEnabled(true);
                            Snackbar.make(loginloyout,"网络连接失败", Snackbar.LENGTH_SHORT).show();

                        }
                    };
                    OkHttpUtils.post(getValidateUserURL, loginCallback, null);//验证用户

                }
            }
        });
    }
}
