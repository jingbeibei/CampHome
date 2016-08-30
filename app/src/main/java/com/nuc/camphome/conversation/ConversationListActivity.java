package com.nuc.camphome.conversation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

/**
 * Created by 景贝贝 on 2016/8/29.
 */
public class ConversationListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private List<Conversation> mData;
    private int pageIndex = 1;
    private ConversationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private String username;
    private TextView BarRightTv;
    private TextView BarTitle;
    private ImageView BackImage;
    FloatingActionButton reportFAB;

    private int type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);
        username = pref.getString("username", "");
        type = Integer.parseInt(getIntent().getStringExtra("type"));

        BarRightTv = (TextView) findViewById(R.id.bar_right_tv);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        if (type == 1) {
            BarTitle.setText("心理服务");
        } else {
            BarTitle.setText("定制谈心");
        }
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        reportFAB = (FloatingActionButton) findViewById(R.id.report_fab);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.primary_light, R.color.colorAccent);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_View);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画

        mAdapter = new ConversationAdapter(this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        onRefresh();

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ConversationListActivity.this);
            }
        });
        reportFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //184350  mRecyclerView.setSelected(false);
                Intent intent = new Intent(ConversationListActivity.this, ReleaseRepportActivity.class);
                intent.putExtra("flag",flag+"");
                startActivity(intent);
            }
        });
    }

    //刷新
    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (mData != null) {
            mData.clear();
        }
        if (pageIndex == 1) {
            showProgress();
        }
        loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetConversationsURL);
    }

    public void showProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    //通过网络获取数据
    public void loadDate(int Index, int size, String url) {

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
        url = url + "times=" + times + "&code=" + code + "&applicationID=" + applicationid + "&username=" + username + " &pageindex=" + Index + "&pagesize=" + size + "&type=" + type;
        OkHttpUtils.post(url, loadConversationCallback, null);
        // OkHttpUtils.get(url, loadReportCallback);
    }

    public void addonSuccess(List<Conversation> list) {
        hideProgress();
        addReports(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }

    public void addReports(List<Conversation> reportList) {
        mAdapter.isShowFooter(true);
        if (mData == null) {
            mData = new ArrayList<Conversation>();
        }
        mData.addAll(reportList);
        if (pageIndex == 1) {
            mAdapter.setmDate(mData);
        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (reportList == null || reportList.size() == 0) {
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
    private ConversationAdapter.OnItemClickListener mOnItemClickListener = new ConversationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//             Conversation report = mAdapter.getItem(position);
//            Intent intent = new Intent(ReportListActivity.this, ReportItemActivity.class);
//            intent.putExtra("report", report);
//            intent.putExtra("tijiao", "2");
//
//            startActivity(intent);
//            Toast.makeText()

        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()
                    && mAdapter.isShowFooter()) {
                //加载更多
                loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetConversationsURL);
            }
        }
    };
}
