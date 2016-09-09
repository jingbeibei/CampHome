package com.nuc.camphome.main.personalcenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.Personnel;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;
import com.nuc.camphome.utils.ToolBase64;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by 景贝贝 on 2016/9/6.
 */
public class PersonalCenterFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private SharedPreferences.Editor editor;

    private Personnel personnel;
    private ImageView headImage;
    private TextView nicknameTV;
    private LinearLayout modifyData;
    private LinearLayout modifyPassword;
    private Button exitBtn;

    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;
    private String myUpdatePersonnelUrl;

    private TextView BarTitle;
    private ImageView BackImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personnel = (Personnel) getArguments().getSerializable("personnel");
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        editor = pref.edit();
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        headImage = (ImageView) view.findViewById(R.id.setting_head_image);
        nicknameTV = (TextView) view.findViewById(R.id.setting_nickname);
        modifyData = (LinearLayout) view.findViewById(R.id.modify_data_layout);
        modifyPassword = (LinearLayout) view.findViewById(R.id.modify_password_layout);
        BackImage = (ImageView) view.findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) view.findViewById(R.id.id_bar_title);
        BackImage.setVisibility(View.INVISIBLE);
        BarTitle.setText("个人中心");
        exitBtn = (Button) view.findViewById(R.id.id_exit_btn);
        if (personnel != null) {
            Picasso.with(getContext()).load(personnel.getPictureUrl()).placeholder(R.mipmap.protrait).error(R.mipmap.protrait).into(headImage);
            nicknameTV.setText(personnel.getNickname());
        }

        initEvent();
        return view;
    }

    private void initEvent() {
        headImage.setOnClickListener(this);
        modifyData.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_head_image:
                MultiImageSelector.create(getActivity())
                        .showCamera(true) // 是否显示相机. 默认为显示
                        .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                        .single() // 单选模式
                        // .multi() // 多选模式, 默认模式;
                        .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
                        .start(PersonalCenterFragment.this, REQUEST_IMAGE);
                break;

            case R.id.modify_data_layout:
                Intent dataIntent = new Intent(getActivity(), ModifyDateActivity.class);
                dataIntent.putExtra("personnel", personnel);
                startActivity(dataIntent);
                break;
            case R.id.modify_password_layout:
                Intent passwordIntent = new Intent(getActivity(), ModifyPasswordActivity.class);
                startActivity(passwordIntent);
                break;
            case R.id.id_exit_btn:
                editor.clear();
                editor.commit();
                ActivityCollector.finishAll();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    //sb.append("\n");
                }
                Bitmap bitmap = BitmapFactory.decodeFile(sb.toString());
                headImage.setImageBitmap(bitmap);
                String base64Image = ToolBase64.bitmapToBase64(bitmap);
                updateHeadImage(base64Image);
            }
        }
    }

    private void updateHeadImage(String base64Image) {
        myUpdatePersonnelUrl = Urls.UpdatePersonnelURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&username=" + username;
        List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
        OkHttpUtils.Param param1 = new OkHttpUtils.Param("name", "");
        OkHttpUtils.Param param2 = new OkHttpUtils.Param("nickname", "");
        OkHttpUtils.Param param3 = new OkHttpUtils.Param("picCode", base64Image);
//       LogUtils.i("头像路径modifyactivity",personnel.getPictureUrl());
        params.add(param1);
        params.add(param2);
        params.add(param3);


        OkHttpUtils.ResultCallback<String> updateHeadImageCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {//更新个人信息成功，返回NULL或空；否则返回错误信息。
                // 注意：若同时修改多个信息，可能会有的成功，有的不成功
                if (response.equals("\"\"") || response == null) {
                    Snackbar.make(modifyPassword, "亲，修改头像成功哦！", Snackbar.LENGTH_SHORT).show();
                    //  Toast.makeText(getActivity(), "亲，修改头像成功哦！", Toast.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(modifyPassword, response, Snackbar.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Snackbar.make(modifyPassword, "网络连接失败", Snackbar.LENGTH_SHORT).show();

            }
        };
        OkHttpUtils.post(myUpdatePersonnelUrl, updateHeadImageCallback, params);
    }
}
