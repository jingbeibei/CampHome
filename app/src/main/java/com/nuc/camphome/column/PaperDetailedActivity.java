package com.nuc.camphome.column;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuc.camphome.R;
import com.nuc.camphome.beans.News;
import com.nuc.camphome.beans.Paper;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.JsonUtils;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class PaperDetailedActivity extends AppCompatActivity {
    private TextView title;
    private WebView content;
    private String contentString;
    String head = "<html><head></head><body>";
    String end = "</body></html>";
    private TextView BarTitle;
    private ImageView BackImage;
    private ProgressBar progressBar;

    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private int ColumnPaperID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_detailed);
        ActivityCollector.addActivity(this);
        ColumnPaperID = Integer.parseInt(getIntent().getStringExtra("ColumnPaperID"));
        title = (TextView) findViewById(R.id.paper_title_tv);
        content = (WebView) findViewById(R.id.paper_content_web);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("阅读");

        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(PaperDetailedActivity.this);
            }
        });

        initDate(ColumnPaperID);
        initWeb();
    }

    private void initWeb() {
        WebChromeClient m_chromeClient = new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
            }
        };

        //视频设置
        content.setWebChromeClient(m_chromeClient);

        //	contentWebView.getSettings().setLoadsImagesAutomatically(true);
        content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //		contentWebView.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setDefaultTextEncodingName("utf-8");
        content.getSettings().setDefaultFontSize(22);
        content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


    }

    private void initDate(int id) {

        OkHttpUtils.ResultCallback<String> loadpaperDetailedCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("Title") > 0) {
                    Paper paper = JsonUtils.deserialize(response, Paper.class);
                    contentString = paper.getContents();
                    if (contentString != null) {
                        contentString = contentString.replace(";", "");
                    }
                    contentString = head + contentString + end;
                    content.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);
                    title.setText(paper.getTitle());
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(PaperDetailedActivity.this, "暂未文章", Toast.LENGTH_LONG);
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
                Toast.makeText(PaperDetailedActivity.this, error, Toast.LENGTH_LONG);
            }
        };
        String url = Urls.GetPaperURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&paperID=" + ColumnPaperID;
        OkHttpUtils.post(url, loadpaperDetailedCallback, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
       content.destroy();
    }
}
