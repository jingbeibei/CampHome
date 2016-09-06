package com.nuc.camphome.main.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.utils.ActivityCollector;

public class AboutActivity extends AppCompatActivity {
    private TextView versionTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActivityCollector.addActivity(this);
        versionTV= (TextView) findViewById(R.id.guanyuVersionnameTv);
        PackageInfo info = null;
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = info.versionCode;//获取版本号
        versionTV.setText("WiFi用起来"+version);
    }
}
