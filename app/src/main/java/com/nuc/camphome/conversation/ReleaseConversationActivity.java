package com.nuc.camphome.conversation;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        if (type.equals("2")) {
            InstructorLY.setVisibility(View.VISIBLE);
        }
        //获取导员信息
        initDate();
        initEvent();
    }

    private void initDate() {
        OkHttpUtils.ResultCallback<String> loadConversationCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("Title") > 0) {
                    List<Conversation> conversationList = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<Conversation>>() {
                    }.getType());
                    addonSuccess(conversationList);
                } else {
                    List<Conversation> conversationList1 = new ArrayList<Conversation>();
                    addonSuccess(conversationList1);
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
                addonFailure(error);
            }
        };
       String url = Urls.GetInstructorsURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid ;
        OkHttpUtils.post(url, loadConversationCallback, null);
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

            }
        });
    }
}
