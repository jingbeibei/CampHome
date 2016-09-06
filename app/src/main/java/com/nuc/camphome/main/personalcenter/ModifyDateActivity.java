package com.nuc.camphome.main.personalcenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nuc.camphome.R;
import com.nuc.camphome.beans.Personnel;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.LogUtils;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class ModifyDateActivity extends AppCompatActivity {
    private EditText nicknameET;
    private EditText nameET;
    private Button modifyBtn;
    private TextView barTitle;
    private TextView barRight;
    private ImageView backIV;

    private Personnel personnel;
    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private String myUpdatePersonnelUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_date);
        ActivityCollector.addActivity(this);
        personnel = (Personnel) getIntent().getSerializableExtra("personnel");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");
        initView();
        initEvent();
    }

    private void initView() {
        nicknameET = (EditText) findViewById(R.id.date_nickname_et);
        nameET = (EditText) findViewById(R.id.data_name_et);
        modifyBtn = (Button) findViewById(R.id.modify_data_btn);
        barTitle = (TextView) findViewById(R.id.id_bar_title);
        barRight = (TextView) findViewById(R.id.bar_right_tv);
        backIV= (ImageView) findViewById(R.id.id_back_arrow_image);
        nicknameET.setText(personnel.getNickname());
        nameET.setText(personnel.getName());
        barTitle.setText("修改资料");
        barRight.setText("");
    }

    private void initEvent() {
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String nickname = nicknameET.getText().toString();
                modifyBtn.setText("修改中...");
                modifyBtn.setEnabled(false);
                if (nickname.equals("") || name.equals("")) {
                    Snackbar.make(nicknameET, "昵称和姓名不能为空！", Snackbar.LENGTH_SHORT).show();
                    modifyBtn.setText("修改");
                    modifyBtn.setEnabled(true);
                    return;
                }

                myUpdatePersonnelUrl = Urls.UpdatePersonnelURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid+"&username="+username;
                List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
                OkHttpUtils.Param param1 = new OkHttpUtils.Param("name", name);
                OkHttpUtils.Param param2 = new OkHttpUtils.Param("nickname", nickname);
                OkHttpUtils.Param param3 = new OkHttpUtils.Param("picCode", personnel.getPictureUrl());
                LogUtils.i("头像路径modifyactivity",personnel.getPictureUrl());
                params.add(param1);
                params.add(param2);
                params.add(param3);


                OkHttpUtils.ResultCallback<String> updatePersonnelCallback = new OkHttpUtils.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String response) {//更新个人信息成功，返回NULL或空；否则返回错误信息。
                        // 注意：若同时修改多个信息，可能会有的成功，有的不成功
                        if (response.equals("\"\"") || response == null) {
                            Toast.makeText(ModifyDateActivity.this, "亲，修改个人信息成功哦！", Toast.LENGTH_SHORT).show();
                            nicknameET.setText("");
                            nameET.setText("");
                            finish();
                        } else {
                            Snackbar.make(nameET, response, Snackbar.LENGTH_SHORT).show();
                            modifyBtn.setText("修改");
                            modifyBtn.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Snackbar.make(nameET, "网络连接失败", Snackbar.LENGTH_SHORT).show();
                        modifyBtn.setText("修改");
                        modifyBtn.setEnabled(true);
                    }
                };
                OkHttpUtils.post(myUpdatePersonnelUrl, updatePersonnelCallback, params);
            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ModifyDateActivity.this);

            }
        });
    }


}
