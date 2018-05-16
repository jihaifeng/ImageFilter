package com.jihf.camerasdk.base;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jihf.camerasdk.R;
import com.jihf.camerasdk.utils.CameraToast;


public abstract class BaseCameraActivity extends BasePermissionActivity {
    private static final String TAG = BaseCameraActivity.class.getSimpleName().trim();
    protected Context mContext;
    private TextView tvCameraBack, tvCameraTitle, tvCameraNext;
    private RelativeLayout rlCameraBaseToolbar;
    private View cameraToolbarLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//不可横屏幕

        //布局注入
        setContentView(getLayoutId());

        initBaseView();

        initViewAndEvent();

    }

    private void initBaseView() {
        rlCameraBaseToolbar = getView(R.id.rl_camera_toolbar);

        tvCameraBack = getView(R.id.tv_back);
        tvCameraBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvCameraTitle = getView(R.id.tv_title);
        tvCameraTitle.setText("");

        tvCameraNext = getView(R.id.tv_next);
        tvCameraNext.setVisibility(View.GONE);

        cameraToolbarLine = getView(R.id.camera_toolbar_line);

    }

    public void hideCameraToolbarLine() {
        if (null != cameraToolbarLine) {
            cameraToolbarLine.setVisibility(View.GONE);
        }
    }

    public RelativeLayout getRlCameraBaseToolbar() {
        return rlCameraBaseToolbar;
    }

    public void setToolbarColor(int color) {
        if (null != rlCameraBaseToolbar) {
            rlCameraBaseToolbar.setBackgroundColor(color);
        }
    }

    private void hideTvBack() {
        if (null != tvCameraBack) {
            tvCameraBack.setVisibility(View.GONE);
        }
    }


    public void setTvCameraBack(String text) {
        if (!TextUtils.isEmpty(text)) {
            setTvCameraBack(text, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public void setTvCameraBack(String text, View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(text) && null != tvCameraBack) {
            tvCameraBack.setText(text);
            tvCameraBack.setOnClickListener(onClickListener);
        }
    }


    public void setTvCameraTitle(String title) {
        setTvCameraTitle(title,null);
    }

    public TextView getTvCameraTitle() {
        return tvCameraTitle;
    }

    public void setTvCameraTitle(String title, View.OnClickListener onClickListener) {
        if (null != tvCameraTitle) {
            tvCameraTitle.setText(title);
            tvCameraTitle.setOnClickListener(onClickListener);
        }
    }

    public void setTvCameraNext(String text) {
        if (!TextUtils.isEmpty(text)) {
            setTvCameraNext(text, null);
        }
    }

    public void setTvCameraNext(String text, View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(text) && null != tvCameraNext) {
            tvCameraNext.setText(text);
            tvCameraNext.setVisibility(View.VISIBLE);
            tvCameraNext.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        try {
            View view = getLayoutInflater().inflate(R.layout.camera_activity_base, null);
            FrameLayout frameLayout = getView(view, R.id.content_frame);
            getLayoutInflater().inflate(layoutResID, frameLayout, true);
            super.setContentView(view);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public final <E extends View> E getView(View parentView, int id) {
        try {
            if (null != parentView) {
                return (E) parentView.findViewById(id);
            } else {
                throw new NullPointerException("parentView is null.");
            }
        } catch (Exception e) {
            Log.i(TAG, "Can not cast view to concrete class" + e);
            throw e;
        }
    }

    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (Exception e) {
            Log.i(TAG, "Can not cast view to concrete class" + e);
            throw e;
        }
    }

    public void showToast(String toastStr) {
        CameraToast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }


    protected abstract int getLayoutId();

    protected abstract void initViewAndEvent();

}
