package com.nuc.camphome.suggest;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.SuggestThem;
import com.nuc.camphome.beans.Suggest;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class SuggestDetailedActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private TextView BarTitle;
    private ImageView BackImage;
    private TextView suggestTitleTV, suggestContentTV, suggestTimeTV, suggestRemarkTV;
    private EditText suggestMyRemarkET;
    private Button suggestReleaseBT;
    private SuggestThem suggesThem;
    private int themeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_detailed);
        ActivityCollector.addActivity(this);
        suggesThem = (SuggestThem) getIntent().getSerializableExtra("suggestThem");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");
        initView();
        initEvent();
    }


    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        suggestTitleTV = (TextView) findViewById(R.id.suggest_title_tv);
        suggestContentTV = (TextView) findViewById(R.id.suggest_content_tv);
        suggestTimeTV = (TextView) findViewById(R.id.suggest_time_tv);
        suggestRemarkTV = (TextView) findViewById(R.id.suggest_remarks_tv);
        suggestMyRemarkET = (EditText) findViewById(R.id.suggest_myremark_et);
        suggestReleaseBT = (Button) findViewById(R.id.suggest_release_button);

        BarTitle.setText("在线调查");
        suggestTitleTV.setText(suggesThem.getTitle());
        suggestTimeTV.setText(suggesThem.getPublishTime().substring(0, 10));
        suggestContentTV.setText(suggesThem.getContents());
        themeID = suggesThem.getID();
        List<Suggest> suggests = suggesThem.getSuggests();
        String allRemark = "";
        if (suggests != null) {
            for (Suggest s : suggests) {
                allRemark += s.getSubmitTime().substring(6, 10) + "评论：" + s.getContents() + "\n";
            }
        }
        suggestRemarkTV.setText(allRemark);
    }

    private void initEvent() {
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(SuggestDetailedActivity.this);
            }
        });

        suggestReleaseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = suggestMyRemarkET.getText().toString();
                if (content.equals("")) {
                    Toast.makeText(getApplicationContext(), "亲，评论内容不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                String url = Urls.PostSuggestURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid;
                List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
                OkHttpUtils.Param param1 = new OkHttpUtils.Param("themeID", themeID + "");
                OkHttpUtils.Param param2 = new OkHttpUtils.Param("username", username);
                OkHttpUtils.Param param3 = new OkHttpUtils.Param("contents", content);
                OkHttpUtils.Param param4 = new OkHttpUtils.Param("imei", "1");
                params.add(param1);
                params.add(param2);
                params.add(param3);
                params.add(param4);

                //提交谈话
                OkHttpUtils.ResultCallback<String> postLetterCallback = new OkHttpUtils.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        int flag = Integer.parseInt(response);
                        if (flag > 0) {
                            Toast.makeText(SuggestDetailedActivity.this, "提交成功！等待审核", Toast.LENGTH_SHORT).show();
                            suggestMyRemarkET.setText("");

                        } else {
                            Toast.makeText(SuggestDetailedActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                            suggestReleaseBT.setEnabled(true);
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
                        initFailure(error);
                    }
                };

                OkHttpUtils.post(url, postLetterCallback, params);
            }
        });
    }

    public void initFailure(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
