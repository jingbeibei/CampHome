package com.nuc.camphome.main.setting;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.LastApp;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.main.setting.receiver.UpdateReceiver;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.JsonUtils;
import com.nuc.camphome.utils.OkHttpUtils;

/**
 * Created by 景贝贝 on 2016/9/6.
 */
public class SettingFragment extends Fragment{
    private TextView BarTitle;
    private ImageView BackImage;
    private LinearLayout updateLy;
    private LinearLayout aboutLy;

    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;

    private String myGetLastAppUrl;
    OkHttpUtils.ResultCallback<String> GetLastAPPCallback;
    LastApp lastApp;
    UpdateReceiver mUpdateReceiver;
    IntentFilter mIntentFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);

        myGetLastAppUrl = Urls.GetLastAPPURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid;
        GetLastAPPCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("VersionID") > 0) {
                    lastApp = JsonUtils.deserialize(response, LastApp.class);
                    Intent intent=new Intent(UpdateReceiver.UPDATE_ACTION);
                    intent.putExtra("LastApp",lastApp);
                    getActivity().sendBroadcast(intent);
                } else {
                    Snackbar.make(updateLy, response, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                //  LogUtils.e("modelImple","加载个人信息失败");
                e.printStackTrace();
                Snackbar.make(updateLy, "网络连接错误", Snackbar.LENGTH_SHORT).show();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_setting,container,false);
        BackImage = (ImageView) view.findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) view.findViewById(R.id.id_bar_title);
        BackImage.setVisibility(View.INVISIBLE);
        BarTitle.setText("设置");
        updateLy= (LinearLayout) view.findViewById(R.id.update_data_layout);
        aboutLy= (LinearLayout) view.findViewById(R.id.about_layout);
        initEvent();
        return view;
    }

    private void initEvent() {
        updateLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OkHttpUtils.post(myGetLastAppUrl, GetLastAPPCallback,null);
            }
        });

        aboutLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AboutActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterBroadcast();
    }

    /**
     * 广播注册
     */
    private void registerBroadcast() {
        mUpdateReceiver = new UpdateReceiver(false);
        mIntentFilter = new IntentFilter(UpdateReceiver.UPDATE_ACTION);
        getActivity().registerReceiver(mUpdateReceiver, mIntentFilter);
    }

    /**
     * 广播卸载
     */
    private void unRegisterBroadcast() {
        try {
            getActivity().unregisterReceiver(mUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
