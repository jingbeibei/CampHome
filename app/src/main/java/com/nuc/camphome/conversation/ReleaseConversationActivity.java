package com.nuc.camphome.conversation;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuc.camphome.R;
import com.nuc.camphome.beans.Conversation;
import com.nuc.camphome.beans.Personnel;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class ReleaseConversationActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private TextView BarRightTv;
    private TextView BarTitle;
    private ImageView BackImage;
    private EditText ConversationReleaseTitle;
    private EditText ConversationReleaseContent;
    private Button ConversationReleaseBtn;
    private Spinner InstructorSp;
    private String type;
    private LinearLayout InstructorLY;
    private ArrayList<Personnel> myPersonnes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_conversation);
        ActivityCollector.addActivity(this);
        type = getIntent().getStringExtra("type");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");

        ConversationReleaseTitle = (EditText) findViewById(R.id.conversation_release_title_et);
        ConversationReleaseContent = (EditText) findViewById(R.id.conversation_releasw_content_et);
        ConversationReleaseBtn = (Button) findViewById(R.id.conversation_release_button);
        InstructorSp = (Spinner) findViewById(R.id.Spinnerconver);
        InstructorLY = (LinearLayout) findViewById(R.id.instructor_ly);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("提交");

        if (type.equals("2")) {
            InstructorLY.setVisibility(View.VISIBLE);
        }
        //获取导员信息
        initDate();
        initEvent();
    }

    //初始化数据
    private void initDate() {
        OkHttpUtils.ResultCallback<String> loadInstructorCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("Name") > 0) {
                    List<Personnel> personnelList = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<Personnel>>() {
                    }.getType());
                    initSpannerSuccess(personnelList);
                } else {
                    List<Personnel> personnelList = new ArrayList<Personnel>();
                    initSpannerSuccess(personnelList);
                    // listener.onFailure("load report list failure.");
                }

            }

            @Override
            public void onFailure(Exception e) {
                String error = "";
                if (e.toString().contains("java.net.ConnectException")) {
                    error = "网络连接失败";
                } else {
                    error = "未知错误" + e.toString();
                }
                initSpannerFailure(error);
            }
        };
        String url = Urls.GetInstructorsURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid;
        OkHttpUtils.post(url, loadInstructorCallback, null);

    }

    private void initEvent() {
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ReleaseConversationActivity.this);
            }
        });
        ConversationReleaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ConversationReleaseTitle.getText().toString();
                String content = ConversationReleaseContent.getText().toString();
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(getApplicationContext(), "亲，标题和内容不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                String instructorName = myPersonnes.get((int) InstructorSp.getSelectedItemId()).getName();
                String url = Urls.PostConversationURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&instructor=" + instructorName + "&type=" + type;
                List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
                OkHttpUtils.Param param1 = new OkHttpUtils.Param("title", title);
                OkHttpUtils.Param param2 = new OkHttpUtils.Param("username", username);
                OkHttpUtils.Param param3 = new OkHttpUtils.Param("contents", content);
                OkHttpUtils.Param param4 = new OkHttpUtils.Param("imei", "1");
                params.add(param1);
                params.add(param2);
                params.add(param3);
                params.add(param4);

                //提交谈话
                OkHttpUtils.ResultCallback<String> postConversationCallback = new OkHttpUtils.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        int flag = Integer.parseInt(response);
                        if (flag > 0) {
                            Toast.makeText(ReleaseConversationActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                            ConversationReleaseTitle.setText("");
                            ConversationReleaseContent.setText("");
                            finish();
                        } else {
                            Snackbar.make(ConversationReleaseContent, "提交失败", Snackbar.LENGTH_SHORT).show();
                            ConversationReleaseBtn.setEnabled(true);
                        }

                    }

                    @Override
                    public void onFailure(Exception e) {
                        String error = "";
                        if (e.toString().contains("java.net.ConnectException")) {
                            error = "网络连接失败";
                        } else {
                            error = "未知错误" + e.toString();
                        }
                        initSpannerFailure(error);
                    }
                };

                OkHttpUtils.post(url, postConversationCallback,params);

            }
        });
    }

    public void initSpannerSuccess(List<Personnel> personnels) {
        if (personnels != null) {
            myPersonnes.addAll(personnels);
            String[] username = new String[personnels.size()];
            for (int i = 0; i < personnels.size(); i++) {
                username[i] = personnels.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReleaseConversationActivity.this, android.R.layout.simple_spinner_item, username);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            InstructorSp.setAdapter(adapter);
        } else {
            String[] username = new String[personnels.size()];
            username[0] = "暂无指导员";
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReleaseConversationActivity.this, android.R.layout.simple_spinner_item, username);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            InstructorSp.setAdapter(adapter);
        }
    }

    public void initSpannerFailure(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
