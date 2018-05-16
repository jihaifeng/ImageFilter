package com.jihf.camerasdk.utils;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jihf.camerasdk.R;


/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-11-15-0015 10:42
 * Mail：jihaifeng@meechao.com
 */
public class CameraToast {

  private static Toast mToast;
  private static TextView tvContent;
  private static int mDuration;

  private static Handler mHandler = new Handler();
  private static Runnable r = new Runnable() {
    public void run() {
      if (mToast != null) {
        mToast.cancel();
      }
    }
  };

  private CameraToast(Context context, CharSequence text, int duration) {

    if (null == mToast) {
      mToast = new Toast(context);
      View v = LayoutInflater.from(context).inflate(R.layout.camera_toast_view, null);
      tvContent = v.findViewById(R.id.tv_content);
      mToast.setView(v);
    }
    tvContent.setText(text);

    mDuration =
        duration == Toast.LENGTH_SHORT ? 1000 : (duration == Toast.LENGTH_LONG ? 2000 : duration);
  }

  /**
   *  实例化
   * @param context 上下文
   * @param text 提示内容
   * @param duration 提示显示时间 ，单位 ms
   * @return this
   */
  public static CameraToast makeText(Context context, CharSequence text, int duration) {
    return new CameraToast(context, text, duration);
  }

  public void show() {
    if (mToast != null) {
      mHandler.removeCallbacks(r);
      mToast.setDuration(mDuration);
      mToast.show();
      mHandler.postDelayed(r, mDuration);
    }
  }

  public void dimiss() {
    if (mToast != null) {
      mToast.cancel();
    }
  }

  public CameraToast setGravity(int gravity, int xOffset, int yOffset) {
    if (mToast != null) {
      mToast.setGravity(gravity, xOffset, yOffset);
    }
    return this;
  }
}
