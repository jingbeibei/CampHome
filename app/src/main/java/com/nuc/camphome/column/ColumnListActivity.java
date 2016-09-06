package com.nuc.camphome.column;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuc.camphome.R;
import com.nuc.camphome.beans.ColumnPaper;
import com.nuc.camphome.beans.Columns;
import com.nuc.camphome.commons.Urls;
import com.nuc.camphome.news.NewsDetailedActivity;
import com.nuc.camphome.utils.ActivityCollector;
import com.nuc.camphome.utils.GetTimesAndCode;
import com.nuc.camphome.utils.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class ColumnListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private List<ColumnPaper> mData;
    private int pageIndex = 1;
    private PaperAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    private SharedPreferences pref;
    private long applicationid = 1;
    private String times;
    private String code;
    private TextView BarTitle;
    private ImageView BackImage;
    private int columnID;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_columns_list);
        ActivityCollector.addActivity(this);
      columnID=Integer.parseInt(getIntent().getStringExtra("ColumnID"));
        title=getIntent().getStringExtra("Title");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        times = GetTimesAndCode.getTimes();
        code = GetTimesAndCode.getCode(times);
        applicationid = pref.getLong("applicationID", 1);

        initView();
        onRefresh();

    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);

        BarTitle.setText(title);

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

        mAdapter = new PaperAdapter(this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ColumnListActivity.this);
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
        loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetPapersByColumnURL);
    }

    public void showProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    //通过网络获取数据
    public void loadDate(int Index, int size, String url) {

        OkHttpUtils.ResultCallback<String> loadColumnCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (response.indexOf("Title") > 0) {
                    List<ColumnPaper> columnList = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<ColumnPaper>>() {
                    }.getType());
                    addonSuccess(columnList);
                } else {
                    List<ColumnPaper> columnsList1 = new ArrayList<ColumnPaper>();
                    addonSuccess(columnsList1);
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
        url = url + "times=" + times + "&code=" + code + "&applicationID=" + applicationid +"&columnID="+columnID+"&pageIndex="+pageIndex+"&pageSize="+size;
        OkHttpUtils.post(url, loadColumnCallback, null);
        // OkHttpUtils.get(url, loadReportCallback);
    }

    public void addonSuccess(List<ColumnPaper> list) {
        hideProgress();
        addNews(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }

    public void addNews(List<ColumnPaper> columnsList) {
        mAdapter.isShowFooter(true);
        if (mData == null) {
            mData = new ArrayList<ColumnPaper>();
        }
        if(columnsList!=null){
            mData.addAll(columnsList);}
        if (pageIndex == 1) {
            if(columnsList.size()<Urls.PAZE_SIZE){
                mAdapter.isShowFooter(false);
            }
            mAdapter.setmDate(mData);
        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (columnsList == null || columnsList.size() == 0) {
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
    private PaperAdapter.OnItemClickListener mOnItemClickListener = new PaperAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ColumnPaper columns = mAdapter.getItem(position);
            Intent intent = new Intent(ColumnListActivity.this,PaperDetailedActivity.class);
            intent.putExtra("ColumnPaperID",columns.getColumnPaperID()+"");
            startActivity(intent);

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
                    &&mAdapter.isShowFooter()) {
                //加载更多
                 loadDate(pageIndex, Urls.PAZE_SIZE, Urls.GetPapersByColumnURL);

            }
        }
    };
}

