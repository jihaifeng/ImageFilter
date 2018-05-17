package com.jihf.camerasdk.edit;

import android.app.Activity;
import android.content.Intent;

import com.jihf.camerasdk.selector.ImageSelectProxy;

import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/17 下午4:36
 * Mail：jihaifeng@meechao.com
 */
public class ImageEditProxy {

    private static ImageEditProxy.Builder mBuilder;

    public static ImageEditProxy.Builder with(Activity activity) {
        return new Builder(activity);
    }

    public ImageEditProxy(ImageEditProxy.Builder builder) {
        this.mBuilder = builder;
    }

    public static Builder getBuilder() {
        return mBuilder;
    }

    public void start() {
        Intent intent = new Intent(mBuilder.activity, ImageFilterActivity.class);
        mBuilder.activity.startActivity(intent);
    }

   public static class Builder {
        private Activity activity;
        private ArrayList<String> imageList;
        private ImageEditCallback editCallback;

        public Builder(Activity activity) {
            this.activity = activity;
        }


        public Builder imageList(ArrayList<String> imageList) {
            this.imageList = imageList;
            return this;
        }

        public Builder editCallback(ImageEditCallback editCallback) {
            this.editCallback = editCallback;
            return this;
        }

       public Activity getActivity() {
           return activity;
       }

       public ArrayList<String> getImageList() {
           return imageList;
       }

       public ImageEditCallback getEditCallback() {
           return editCallback;
       }

       public void start() {
            if (null == activity) {
                throw new NullPointerException("Activity is null.please check it first.");
            }
            if (null == imageList || imageList.size() <= 0) {
                throw new NullPointerException("imageList is null.please check it first.");
            }

            new ImageEditProxy(this).start();
        }
    }
}
