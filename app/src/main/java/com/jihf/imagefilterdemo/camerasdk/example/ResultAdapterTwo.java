package com.jihf.imagefilterdemo.camerasdk.example;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jihf.camerasdk.model.ImageInfo;

import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/17 下午5:07
 * Mail：jihaifeng@meechao.com
 */
public class ResultAdapterTwo extends RecyclerView.Adapter<ResultAdapterTwo.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImages;
    private LayoutInflater mInflater;


    public ResultAdapterTwo(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(com.jihf.camerasdk.R.layout.item_camera_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String image = mImages.get(position);
        Glide.with(mContext).load(image).centerCrop().crossFade().into(holder.ivImage);
        holder.ivMasking.setVisibility(View.GONE);
        holder.ivSelectIcon.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return null == mImages ? 0: mImages.size();
    }

    public void refresh(ArrayList<String> as) {
        this.mImages = as;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivSelectIcon;
        ImageView ivMasking;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(com.jihf.camerasdk.R.id.iv_image);
            ivSelectIcon = itemView.findViewById(com.jihf.camerasdk.R.id.iv_select);
            ivMasking = itemView.findViewById(com.jihf.camerasdk.R.id.iv_masking);
        }
    }

    public interface OnImageSelectListener {
        void OnImageSelect(ImageInfo image, boolean isSelect, int selectCount);
    }

    public interface OnItemClickListener {
        void OnItemClick(ImageInfo image, int position);
    }
}
