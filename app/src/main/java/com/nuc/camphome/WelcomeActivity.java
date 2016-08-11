package com.nuc.camphome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nuc.camphome.beans.FlashPicture;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.main.widget.LoginActivity;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.JsonUtils;
import com.nuc.camphome.utils.LogUtils;
import com.nuc.camphome.utils.OkHttpUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WelcomeActivity extends AppCompatActivity {
    /**
     * 闪动屏幕触碰事件类
     */
    private static final String TAG = "WelcomeActivity";
    private Thread mSplashThread;
    private long applicationid = 1;
    private String times;
    private String code;
    private String imei = "";
    private String username = "";
    private int version;
    private int state;//是否为第一次使用
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private String getAppidurl = "", getFlashPictureUrl = "";
    private ImageView flashIV;
    private OkHttpUtils.ResultCallback<String> getFlashPictureCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        flashIV = (ImageView) findViewById(R.id.flashIV);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor = pref.edit();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();//获取手机唯一标识
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = info.versionCode;//获取版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        username = pref.getString("username", "");
        state = pref.getInt("state", 0);

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);// 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();  // 获取当前的网络连接是否可用
        if (networkInfo == null || state == 0) {
            editor.putInt("state", 1);

        } else {
            state = 1;
        }
        getAppidurl = Urls.RegisterUrl + "times=" + times + "&code=" + code + "&state=" + state + "&imei=" + imei + "&username=" + username + "&version=" + version;
        OkHttpUtils.ResultCallback<String> getAppidCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (Integer.parseInt(response) > 0) {
                    applicationid = Integer.parseInt(response);
                    getFlashPictureUrl = Urls.GetLastFlashPicture + "times=" + times + "&code=" + code + "&applicationID=" + applicationid;
                    OkHttpUtils.post(getFlashPictureUrl, getFlashPictureCallback, null);
                } else {
                    applicationid = 1;
                }
            }

            @Override
            public void onFailure(Exception e) {
                applicationid = 0;
            }
        };
        OkHttpUtils.post(getAppidurl, getAppidCallback, null);//获取applicationid


        getFlashPictureCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                FlashPicture mflashPicture = JsonUtils.deserialize(response, FlashPicture.class);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    Date startTime = sdf.parse(mflashPicture.getStartTime());
                    Date endTime = sdf.parse(mflashPicture.getEndTime());
                    int flag1 = sdf.parse(times).compareTo(startTime);
                    int flag2 = endTime.compareTo(sdf.parse(times));
                    if (flag1 >= 0 && flag2 >= 0) {
                        Picasso.with(WelcomeActivity.this)
                                .load(mflashPicture.getPageUrl())
                                .placeholder(R.mipmap.newestflashpic)
                                .error(R.mipmap.newestflashpic)
                                .into(flashIV);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception e) {
                LogUtils.e(TAG, "请求网络图片失败");
            }
        };


        // 启动闪屏界面的线程
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // 闪屏的停留时间
                        wait(3000);
                    }
                } catch (InterruptedException ex) {
                }

                finish();
                editor.putLong("applicationID", applicationid);
                editor.commit();
                //启动下一个Activity
                if (username.equals("")) {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));

                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

                }


                try {
                    stop();
                } catch (Exception e) {
                    e.printStackTrace();
                    // System.out.println(e);
                }
            }
        };

        mSplashThread.start();

    }
}
