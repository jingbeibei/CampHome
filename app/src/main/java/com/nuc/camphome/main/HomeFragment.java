package com.nuc.camphome.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nuc.camphome.R;
import com.nuc.camphome.beans.BannerPic;
import com.nuc.camphome.chat.ChatActivity;
import com.nuc.camphome.column.ColumnListActivity;
import com.nuc.camphome.column.ColumnsListActivity;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.conversation.ConversationListActivity;
import com.nuc.camphome.mailbox.ChiefMailboxActivity;
import com.nuc.camphome.main.ImageSlideshow.ImageSlideshow;
import com.nuc.camphome.medial.MedialListActivity;
import com.nuc.camphome.news.NewsListActivity;
import com.nuc.camphome.suggest.SuggestListActivity;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 景贝贝 on 2016/8/27.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private ImageSlideshow imageSlideshow;
    private List<String> imageUrlList;
    private List<String> titleList;
    private String getBannerURL;
    private List<BannerPic> bannerPicsList = null;
    private ImageView dztxIV, zxjyIV, ptzsIV, jnwxwIV, jxIV, ptkxIV, lhbIV, szxxIV, wlktIV, zxdcIV, xlfwIV, zsscIV, ylyyIV;

    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private int newsType_longhubang = 1;//龙虎榜
    private int newsType_junneiwai = 2;//军内外新闻
    private int newsType_paotuankuaixun = 3;//炮团快讯

    private int medialType_wangluoketang = 1;//网络课堂
    private int medialType_zixuanjiaoyu = 2;//自选教育
    private int medialType_yuleyingyin = 3;//娱乐影音

    public HomeFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");
        getBannerURL = Urls.GetPictureNewsURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&pageSize=" + 5;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlideshow = (ImageSlideshow) view.findViewById(R.id.is_gallery);
        imageUrlList = new ArrayList<>();
        titleList = new ArrayList<>();
        OkHttpUtils.ResultCallback<String> getBannerCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("TitlePicture") > 0) {
                    bannerPicsList = new Gson().fromJson(response, new TypeToken<List<BannerPic>>() {
                    }.getType());
                    // 初始化数据
                    initData();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                String s = e.toString();
//                Snackbar.make(loginloyout,"网络连接失败", Snackbar.LENGTH_SHORT).show();
                if (e.toString().contains("java.net.ConnectException")) {
                    Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "未知错误" + e.toString(), Toast.LENGTH_LONG).show();
                }
                initData();
            }
        };
        OkHttpUtils.post(getBannerURL, getBannerCallback, null);//获取banner图片url
        initView(view);
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {

        String[] imageUrls = {"http://pic3.zhimg.com/b5c5fc8e9141cb785ca3b0a1d037a9a2.jpg",
                "http://pic2.zhimg.com/551fac8833ec0f9e0a142aa2031b9b09.jpg",
                "http://pic2.zhimg.com/be6f444c9c8bc03baa8d79cecae40961.jpg",
                "http://pic1.zhimg.com/b6f59c017b43937bb85a81f9269b1ae8.jpg",
                "http://pic2.zhimg.com/a62f9985cae17fe535a99901db18eba9.jpg"};
        String[] titles = {"读读日报 24 小时热门 TOP 5 · 余文乐和「香港贾玲」乌龙绯闻",
                "写给产品 / 市场 / 运营的数据抓取黑科技教程",
                "学做这些冰冰凉凉的下酒宵夜，简单又方便",
                "知乎好问题 · 有什么冷门、小众的爱好？",
                "欧洲都这么发达了，怎么人均收入还比美国低"};
        if (bannerPicsList != null) {
            for (int i = 0; i < 5; i++) {
//            imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        } else {
            for (int i = 0; i < 5; i++) {
                imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
//                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        }

        // 为ImageSlideshow设置数据        imageSlideshow.setDotSpace(12);
        imageSlideshow.setDotSize(12);
        imageSlideshow.setDelay(3000);
        imageSlideshow.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), "0", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(MainActivity.this,Activity_1.class));
                        break;
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "2", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), "3", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(getContext(), "4", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        imageSlideshow.commit();
    }

    private void initView(View view) {
        dztxIV = (ImageView) view.findViewById(R.id.dztxIV);
        zxjyIV = (ImageView) view.findViewById(R.id.zxjyIV);
        ptzsIV = (ImageView) view.findViewById(R.id.ptzsIV);
        jnwxwIV = (ImageView) view.findViewById(R.id.jnwxwIV);
        jxIV = (ImageView) view.findViewById(R.id.jxIV);
        ptkxIV = (ImageView) view.findViewById(R.id.ptkxIV);
        lhbIV = (ImageView) view.findViewById(R.id.lhbIV);
        szxxIV = (ImageView) view.findViewById(R.id.szxxIV);
        wlktIV = (ImageView) view.findViewById(R.id.wlktIV);
        zxdcIV = (ImageView) view.findViewById(R.id.zxdcIV);
        xlfwIV = (ImageView) view.findViewById(R.id.xlfwIV);
        zsscIV = (ImageView) view.findViewById(R.id.zsscIV);
        ylyyIV = (ImageView) view.findViewById(R.id.ylyyIV);

        dztxIV.setOnClickListener(this);
        zxjyIV.setOnClickListener(this);
        ptzsIV.setOnClickListener(this);
        jnwxwIV.setOnClickListener(this);
        jxIV.setOnClickListener(this);
        ptkxIV.setOnClickListener(this);
        lhbIV.setOnClickListener(this);
        szxxIV.setOnClickListener(this);
        wlktIV.setOnClickListener(this);
        zxdcIV.setOnClickListener(this);
        xlfwIV.setOnClickListener(this);
        zsscIV.setOnClickListener(this);
        ylyyIV.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        // 释放资源
        imageSlideshow.releaseResource();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.dztxIV://定制谈心
                Intent intent = new Intent(getActivity(), ConversationListActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            case R.id.zxjyIV://自选教育
                Intent zxjyIntent = new Intent(getActivity(), MedialListActivity.class);
                zxjyIntent.putExtra("type", medialType_zixuanjiaoyu + "");
                startActivity(zxjyIntent);

                break;
            case R.id.ptzsIV://炮团之声
                Intent ptzsIntent = new Intent(getActivity(), ColumnListActivity.class);
                ptzsIntent.putExtra("ColumnID", 21 + "");
                ptzsIntent.putExtra("Title", "炮团之声");
                startActivity(ptzsIntent);
                break;
            case R.id.jnwxwIV://军内外新闻
                Intent jnwxwIntent = new Intent(getActivity(), NewsListActivity.class);
                jnwxwIntent.putExtra("type", newsType_junneiwai + "");
                startActivity(jnwxwIntent);
                break;
            case R.id.jxIV://军信
                Intent jxIntent = new Intent(getActivity(), ChatActivity.class);
                jxIntent.putExtra("url", Urls.ChatURL + username);
                startActivity(jxIntent);
                break;
            case R.id.ptkxIV://炮团快讯
                Intent ptkxIntent = new Intent(getActivity(), NewsListActivity.class);
                ptkxIntent.putExtra("type", newsType_paotuankuaixun + "");
                startActivity(ptkxIntent);
                break;
            case R.id.lhbIV://龙虎榜
                Intent lhbIntent = new Intent(getActivity(), NewsListActivity.class);
                lhbIntent.putExtra("type", newsType_longhubang + "");
                startActivity(lhbIntent);
                break;
            case R.id.szxxIV://首长信箱
                Intent szxxIntent = new Intent(getActivity(), ChiefMailboxActivity.class);
                startActivity(szxxIntent);
                break;
            case R.id.wlktIV://网络课堂
                Intent wlktIntent = new Intent(getActivity(), MedialListActivity.class);
                wlktIntent.putExtra("type", medialType_wangluoketang + "");
                startActivity(wlktIntent);
                break;
            case R.id.zxdcIV://在线调查
                Intent zxdcIntent = new Intent(getActivity(), SuggestListActivity.class);

                startActivity(zxdcIntent);
                break;
            case R.id.xlfwIV://心理服务
                Intent xlfwIntent = new Intent(getActivity(), ConversationListActivity.class);
                xlfwIntent.putExtra("type", "1");
                startActivity(xlfwIntent);
                break;
            case R.id.zsscIV://掌上书城
                Intent zsscIntent = new Intent(getActivity(), ColumnsListActivity.class);
                startActivity(zsscIntent);
                break;
            case R.id.ylyyIV://娱乐影音
                Intent ylyyIntent = new Intent(getActivity(), MedialListActivity.class);
                ylyyIntent.putExtra("type", medialType_yuleyingyin + "");
                startActivity(ylyyIntent);
                break;

        }

    }
}
