package com.nuc.camphome.column;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.Columns;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 景贝贝 on 2016/9/5.
 */
public class ColumnsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Columns> mData;
    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private OnItemClickListener mOnItemClickListener;

    public ColumnsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmDate(List<Columns> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_columns_layout, parent, false);
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

           Columns columns = mData.get(position);
            if (columns == null) {
                return;
            }
            ((ItemViewHolder) holder).Title.setText(columns.getTitle());
            ((ItemViewHolder) holder).content.setText(columns.getRemarks()+"");

            if (columns.getImageUrl() != null) {
               // ((ItemViewHolder) holder).titleImageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(columns.getImageUrl())
                        .placeholder(R.mipmap.banner_error)
                        .error(R.mipmap.banner_error)
                        .into(((ItemViewHolder) holder).titleImageView);
            }

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

    public Columns getItem(int position) {
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

        //        public TextView mTitle;
        public ImageView titleImageView;
        public TextView Title, content;

        public ItemViewHolder(View v) {
            super(v);
            Title = (TextView) v.findViewById(R.id.columns_title_tv);
            content = (TextView) v.findViewById(R.id.columns_content_tv);
            titleImageView = (ImageView) v.findViewById(R.id.columns_image_iv);

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

