package com.jihf.camerasdk.selector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.model.FolderInfo;
import com.jihf.camerasdk.model.ImageInfo;

import java.util.ArrayList;

public class CameraFolderAdapter extends RecyclerView.Adapter<CameraFolderAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<FolderInfo> mFolders;
    private LayoutInflater mInflater;
    private int mSelectItem;
    private OnFolderSelectListener mListener;

    public CameraFolderAdapter(Context context, ArrayList<FolderInfo> folders) {
        mContext = context;
        mFolders = folders;
        this.mInflater = LayoutInflater.from(context);
    }

    public CameraFolderAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }


    public void refresh(ArrayList<FolderInfo> folders) {
        mFolders = folders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FolderInfo folderInfo = mFolders.get(position);
        ArrayList<ImageInfo> images = folderInfo.imageList;
        holder.tvFolderName.setText(folderInfo.name);
        holder.ivSelect.setVisibility(mSelectItem == position ? View.VISIBLE : View.GONE);
        if (null != images) {
            holder.tvFolderSize.setText(String.format("%s张", images.size()));
            if (null != folderInfo.cover) {
                Glide.with(mContext).load(folderInfo.cover.path).centerCrop().crossFade().into(holder.ivImage);
            }
        } else {
            holder.tvFolderSize.setText("0张");
            holder.ivImage.setImageBitmap(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectItem = holder.getAdapterPosition();
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.OnFolderSelect(folderInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolders == null ? 0 : mFolders.size();
    }

    public void setOnFolderSelectListener(OnFolderSelectListener listener) {
        this.mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivSelect;
        TextView tvFolderName;
        TextView tvFolderSize;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvFolderSize = itemView.findViewById(R.id.tv_folder_size);
        }
    }

    public interface OnFolderSelectListener {
        void OnFolderSelect(FolderInfo folder);
    }

}