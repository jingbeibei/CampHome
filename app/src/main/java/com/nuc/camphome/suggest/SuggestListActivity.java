package com.nuc.camphome.suggest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuc.camphome.R;
import com.nuc.camphome.beans.Letter;
import com.nuc.camphome.beans.SuggestThem;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.mailbox.LetterAdapter;
import com.nuc.camphome.mailbox.LetterDetailedActivity;
import com.nuc.camphome.mailbox.WriteLetterActivity;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class SuggestListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private List<SuggestThem> mData;
    private int pageIndex = 1;
    private SuggestAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private TextView BarTitle;
    private ImageView BackImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_list);

        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");

        initView();
        onRefresh();
    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("在线调查");
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.primary_light, R.color.colorAccent);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_View);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        //添加分割线
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

        mAdapter = new SuggestAdapter(this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(SuggestListActivity.this);
            }
        });

    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (mData != null) {
            mData.clear();
        }
        if (pageIndex == 1) {
            showProgress();
        }
        loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetSuggestThemesURL);
    }

    public void showProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    //通过网络获取数据
    public void loadDate(int Index, int size, String url) {

        OkHttpUtils.ResultCallback<String> loadLetterCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("Title") > 0) {
                    List<SuggestThem> suggestThems = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<SuggestThem>>() {
                    }.getType());
                    Log.i("建议",suggestThems+"");
                    addonSuccess(suggestThems);
                } else {
                    List<SuggestThem> suggestThems1 = new ArrayList<SuggestThem>();
                    addonSuccess(suggestThems1);
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
        url = url + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + " &pageindex=" + Index + "&pagesize=" + size;
        OkHttpUtils.post(url, loadLetterCallback, null);
    }

    public void addonSuccess(List<SuggestThem> list) {
        hideProgress();
        addNews(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }

    public void addNews(List<SuggestThem> suggestThems) {
        mAdapter.isShowFooter(true);
        if (mData == null) {
            mData = new ArrayList<SuggestThem>();
        }
        mData.addAll(suggestThems);
        if (pageIndex == 1) {
            if(suggestThems.size()<Urls.PAZE_SIZE){
                mAdapter.isShowFooter(false);
            }
            mAdapter.setmDate(mData);
        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (suggestThems == null || suggestThems.size() == 0) {
                mAdapter.isShowFooter(false);
                Snackbar.make(mRecyclerView, "暂无更多...", Snackbar.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
        }
        pageIndex += 1;
    }

    public void showLoadFailMsg(String error) {
        if (pageIndex == 1) {
            mAdapter.isShowFooter(false);
            mAdapter.notifyDataSetChanged();
        }
        View v1 = mRecyclerView.getRootView();
        Snackbar.make(v1, error, Snackbar.LENGTH_SHORT).show();
    }

    //item点击事件监听
    private SuggestAdapter.OnItemClickListener mOnItemClickListener = new SuggestAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            SuggestThem suggestThem = mAdapter.getItem(position);
            Intent intent = new Intent(SuggestListActivity.this, SuggestDetailedActivity.class);
            intent.putExtra("suggestThem",suggestThem);

            startActivity(intent);

        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();//从0开始
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()
                    &&mAdapter.isShowFooter() ) {
                //加载更多
                loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetSuggestThemesURL);
            }
        }
    };
}
