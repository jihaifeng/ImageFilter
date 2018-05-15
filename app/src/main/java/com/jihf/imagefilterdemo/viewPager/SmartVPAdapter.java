package com.jihf.imagefilterdemo.viewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.HashMap;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/4/17 上午10:35
 * Mail：jihaifeng@meechao.com
 */
public class SmartVPAdapter extends PagerAdapter {
    private static final String TAG = SmartVPAdapter.class.getSimpleName().trim();
    private Context mContext;
    private List<String> urls;
    private SmartVpListener vpListener;
    private HashMap<Integer, ImageView> imageMap = new HashMap<>();

    public SmartVPAdapter(Context mContext, List<String> urls) {
        this(mContext, urls, null);
    }

    public SmartVPAdapter(Context mContext, List<String> urls, SmartVpListener vpListener) {
        this.mContext = mContext;
        this.urls = urls;
        this.vpListener = vpListener;
    }

    public void setVpInterface(SmartVpListener vpListener) {
        this.vpListener = vpListener;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (null != vpListener) {
            vpListener.onLoadImage(imageView, urls.get(position), position);
            Log.i(TAG, "instantiateItem: " + urls.get(position) + " " + position);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vpListener.onItemClick(position);
                }
            });
        }
        imageMap.put(position, imageView);
        container.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = imageMap.get(position);
            if (imageView != null && null != imageView.getDrawable()) {
                Bitmap leftBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                if (leftBitmap != null && !leftBitmap.isRecycled()) {
                    leftBitmap.recycle();
                    leftBitmap = null;
                }
                System.gc();
            }
        container.removeView(((View) object));
    }

    public String getItem(int pos) {
        if (pos >= urls.size()) {
            throw new IndexOutOfBoundsException("pos is larger than photo list length.");
        }
        return urls.get(pos);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        //return super.getItemPosition(object);//默认是不改变
        return POSITION_NONE;//可以即时刷新看源码
    }

    @Override
    public int getCount() {
        return null == urls ? 0 : urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void removeByPosition(int position) {
        if (null != urls && urls.size() > position) {
            urls.remove(position);
            this.notifyDataSetChanged();
        }
    }

    private void releaseImageViewResourse(SubsamplingScaleImageView iv) {
        Bitmap bitmap = iv.getDrawingCache();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            iv.recycle();
        }
        //希望做一次垃圾回收
        System.gc();
    }


    public HashMap<Integer, ImageView> getImageMap() {
        return imageMap;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
