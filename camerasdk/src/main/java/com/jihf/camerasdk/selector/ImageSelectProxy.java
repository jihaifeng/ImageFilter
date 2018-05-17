package com.jihf.camerasdk.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.jihf.camerasdk.model.ImageInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/16 上午11:31
 * Mail：jihaifeng@meechao.com
 */
public class ImageSelectProxy {
    private static ImageSelectProxy.Builder mBuilder;

    public static ImageSelectProxy.Builder with(Activity activity) {
        return new Builder(activity);
    }


    public ImageSelectProxy(ImageSelectProxy.Builder builder) {
        mBuilder = builder;
    }

    public static Builder getBuilder() {
        return mBuilder;
    }

    public void start() {
        Intent intent = new Intent(mBuilder.activity, ImageSelectActivity.class);
        mBuilder.activity.startActivity(intent);
    }


  public  static class Builder {
        private Activity activity;
        private int maxCount = -1;
        private int gridRowCount = 3;
        private ArrayList<ImageInfo> selectList = new ArrayList<>();
        private boolean camera;
        private ImageSelectCallback callback;


        public Builder(Activity activity) {
            this.activity = activity;
        }

        public void start() {
            if (null == activity) {
                throw new NullPointerException("Activity is null.please check it first.");
            }
            new ImageSelectProxy(this).start();
        }

        public ImageSelectProxy.Builder maxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        public ImageSelectProxy.Builder gridRowCount(int gridRowCount) {
            this.gridRowCount = gridRowCount;
            return this;
        }

        public ImageSelectProxy.Builder selectList(ArrayList<ImageInfo> selectList) {
            this.selectList = selectList;
            return this;
        }

        public ImageSelectProxy.Builder camera(boolean camera) {
            this.camera = camera;
            return this;
        }

        public ImageSelectProxy.Builder callback(ImageSelectCallback callback) {
            this.callback = callback;
            return this;
        }

        public ImageSelectCallback getCallback() {
            return callback;
        }

        public boolean needCamera() {
            return camera;
        }

        public ArrayList<ImageInfo> getSelectList() {
            return selectList;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public int getGridRowCount() {
            return gridRowCount;
        }
    }
}
