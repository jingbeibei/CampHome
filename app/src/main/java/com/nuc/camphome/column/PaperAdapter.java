package com.nuc.camphome.column;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.ColumnPaper;
import com.nuc.camphome.beans.News;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 景贝贝 on 2016/9/5.
 */
public class PaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ColumnPaper> mData;
    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private OnItemClickListener mOnItemClickListener;

    public PaperAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmDate(List<ColumnPaper> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_paper_layout, parent, false);
            ItemViewHolder vh = new ItemViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            ColumnPaper news = mData.get(position);
            if (news == null) {
                return;
            }
            ((ItemViewHolder) holder).Title.setText(news.getTitle());
            ((ItemViewHolder) holder).hit.setText(news.getHits() + "");
            ((ItemViewHolder) holder).Time.setText(news.getPublishTime().substring(1,10));
//            Uri uri = Uri.parse(news.getImgsrc());
//            ((ItemViewHolder) holder).mNewsImg.setImageURI(uri);
//            ImageLoaderUtils.display(mContext, ((ItemViewHolder) holder).mNewsImg, news.getImgsrc());
        }
    }

    @Override
    public int getItemCount() {
        int begin = mShowFooter ? 1 : 0;
        if (mData == null) {
            return begin;
        }
        return mData.size() + begin;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (!mShowFooter) {
            return TYPE_ITEM;
        }
        if (position + 1 == getItemCount()) {
            return FOOTER_ITEM;
        } else {
            return TYPE_ITEM;
        }
    }

    public ColumnPaper getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    public void isShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView Title, hit, Time;

        public ItemViewHolder(View v) {
            super(v);
            Title = (TextView) v.findViewById(R.id.pager_title_tv);
            hit = (TextView) v.findViewById(R.id.pager_hit_tv);
            Time = (TextView) v.findViewById(R.id.pager_time_tv);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getPosition());
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }
}

