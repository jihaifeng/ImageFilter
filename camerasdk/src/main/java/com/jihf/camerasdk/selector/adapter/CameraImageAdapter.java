package com.jihf.camerasdk.selector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.model.ImageInfo;
import com.jihf.camerasdk.selector.ImageSelectActivity;
import com.jihf.camerasdk.utils.CameraToast;

import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/16 下午2:47
 * Mail：jihaifeng@meechao.com
 */
public class CameraImageAdapter extends RecyclerView.Adapter<CameraImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ImageInfo> mImages;
    private LayoutInflater mInflater;

    //保存选中的图片
    private ArrayList<ImageInfo> mSelectImages = new ArrayList<>();
    private OnImageSelectListener mSelectListener;
    private OnItemClickListener mItemClickListener;
    private int mMaxCount;
    private boolean isSingle;

    /**
     * @param maxCount 图片的最大选择数量，小于等于0时，不限数量，isSingle为false时才有用。
     */
    public CameraImageAdapter(Context context, int maxCount) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mMaxCount = maxCount;
        this.isSingle = maxCount == 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_camera_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImageInfo image = mImages.get(position);
        if (image.path.equalsIgnoreCase(ImageSelectActivity.CAMERA_PLACE_HOLDER)) {
            Glide.with(mContext).load(R.drawable.camerasdk_icon_camera).centerCrop().crossFade().into(holder.ivImage);
            holder.ivSelectIcon.setVisibility(View.GONE);
            holder.ivMasking.setAlpha(0f);
        } else {
            Glide.with(mContext).load(image.path).centerCrop().crossFade().into(holder.ivImage);
            holder.ivSelectIcon.setVisibility(View.VISIBLE);
            setItemSelect(holder, mSelectImages.contains(image));
            //点击选中/取消选中图片
            holder.ivSelectIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSingle) {
                        //如果是单选，就先清空已经选中的图片，再选中当前图片
                        clearImageSelect();
                        selectImage(image);
                        setItemSelect(holder, true);
                        return;
                    }

                    if (mSelectImages.contains(image)) {
                        //如果图片已经选中，就取消选中
                        unSelectImage(image);
                        setItemSelect(holder, false);
                        return;
                    }

                    if (mMaxCount <= 0 || mSelectImages.size() < mMaxCount) {
                        //如果不限制图片的选中数量，或者图片的选中数量
                        // 还没有达到最大限制，就直接选中当前图片。
                        selectImage(image);
                        setItemSelect(holder, true);
                    } else {
                        CameraToast.makeText(mContext, "已经到达上线", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.OnItemClick(image, holder.getAdapterPosition());
                }
            }
        });
    }

    /**
     * 选中图片
     *
     * @param image
     */
    private void selectImage(ImageInfo image) {
        mSelectImages.add(image);
        if (mSelectListener != null) {
            mSelectListener.OnImageSelect(image, true, mSelectImages.size());
        }
    }

    /**
     * 取消选中图片
     *
     * @param image
     */
    private void unSelectImage(ImageInfo image) {
        mSelectImages.remove(image);
        if (mSelectListener != null) {
            mSelectListener.OnImageSelect(image, false, mSelectImages.size());
        }
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : mImages.size();
    }

    public ArrayList<ImageInfo> getData() {
        return mImages;
    }

    public void refresh(ArrayList<ImageInfo> data) {
        mImages = data;
        notifyDataSetChanged();
    }

    /**
     * 设置图片选中和未选中的效果
     */
    private void setItemSelect(ViewHolder holder, boolean isSelect) {
        if (isSelect) {
            holder.ivSelectIcon.setImageResource(R.drawable.icon_image_select);
            holder.ivMasking.setAlpha(0.7f);
        } else {
            holder.ivSelectIcon.setImageResource(R.drawable.icon_image_un_select);
            holder.ivMasking.setAlpha(0.1f);
        }
    }

    private void clearImageSelect() {
        if (mImages != null && mSelectImages.size() == 1) {
            int index = mImages.indexOf(mSelectImages.get(0));
            if (index != -1) {
                mSelectImages.clear();
                notifyItemChanged(index);
            }
        }
    }

    public void setSelectedImages(ArrayList<ImageInfo> selected) {
        if (mImages != null && mImages.size() > 0 && selected != null && selected.size() > 0) {
            for (ImageInfo selectImage : selected) {
                if (isFull()) {
                    return;
                }
                for (ImageInfo image : mImages) {
                    if (selectImage.path.equals(image.path)) {
                        if (!mSelectImages.contains(image)) {
                            mSelectImages.add(image);
                        }
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    private boolean isFull() {
        if (isSingle && mSelectImages.size() == 1) {
            return true;
        } else if (mMaxCount > 0 && mSelectImages.size() == mMaxCount) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ImageInfo> getSelectImages() {
        return mSelectImages;
    }

    public void setOnImageSelectListener(OnImageSelectListener listener) {
        this.mSelectListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        ImageView ivSelectIcon;
        ImageView ivMasking;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            ivSelectIcon = itemView.findViewById(R.id.iv_select);
            ivMasking = itemView.findViewById(R.id.iv_masking);
        }
    }

    public interface OnImageSelectListener {
        void OnImageSelect(ImageInfo image, boolean isSelect, int selectCount);
    }

    public interface OnItemClickListener {
        void OnItemClick(ImageInfo image, int position);
    }
}
