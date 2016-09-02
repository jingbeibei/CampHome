package com.nuc.camphome.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.nuc.camphome.R;
import com.nuc.camphome.beans.News;
import com.nuc.camphome.utils.ActivityCollector;

public class NewsDetailedActivity extends AppCompatActivity {
    private News news;
    private TextView title;
    private WebView content;
    private String contentString;
    String head = "<html><head></head><body>";
    String end = "</body></html>";
    private TextView BarTitle;
    private ImageView BackImage;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);
        ActivityCollector.addActivity(this);
        news = (News) getIntent().getSerializableExtra("news");
        type=Integer.parseInt(getIntent().getStringExtra("type"));
        title = (TextView) findViewById(R.id.news_title_tv);
        content = (WebView) findViewById(R.id.news_content_web);
        title.setText(news.getTitle());
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        contentString = news.getContents();

        initWeb();
        if (type == 1) {
            BarTitle.setText("龙虎榜");
        }
        if (type == 2) {
            BarTitle.setText("军内外新闻");
        }
        if (type == 3) {
            BarTitle.setText("炮团快讯");
        }
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(NewsDetailedActivity.this);
            }
        });
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
        if (contentString != null) {
            contentString = contentString.replace(";", "");
        }
        contentString = head + contentString + end;
        content.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);

    }
}
