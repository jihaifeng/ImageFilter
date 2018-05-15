package com.jihf.imagefilterdemo.viewPager;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class ScreenUtil {

    private static Context mContext;
    private static ScreenUtil screen;
    private static float screenWidth = -1;
    private static float screenHeight = -1;
    private static int stateBarHeight = -1;
    private static float density = 1;
    private static float scale;

    private ScreenUtil(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        // 屏幕密度（0.75 / 1.0 / 1.5）
        density = dm.density;
        stateBarHeight = getStateBarHeight(mContext);
        scale = mContext.getResources().getDisplayMetrics().density;
    }

    public static ScreenUtil getInstance(Context context) {
        mContext = context;
        if (screen == null) {
            synchronized (ScreenUtil.class) {
                if (null == screen) {
                    screen = new ScreenUtil(context);
                }
            }
        }
        return screen;
    }

    public static float getScreenWidth() {
        return screenWidth;
    }

    public static float getScreenHeight() {
        return screenHeight;
    }

    public static float getDensity() {
        return density;
    }

    public static int getStateBarHeight() {
        return stateBarHeight;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStateBarHeight(Context mContext) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mContext.getResources().getDimensionPixelSize(x);
            return sbar;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static int dip2px(float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * SP 转 Pixels
     *
     * @param sp sp 字体大小
     * @return pixels
     */
    public static float sp2Px(float sp) {
        return sp * scale;
    }

    /**
     * dp转px
     *
     * @return px
     */

    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, mContext.getResources().getDisplayMetrics());

    }


    /**
     * sp转px
     *
     * @return px
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, mContext.getResources().getDisplayMetrics());

    }


    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */

    public static float px2dp(Context context, float pxVal) {
        return (pxVal / scale);
    }


    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */

    public static float px2sp(float pxVal) {

        return (pxVal / mContext.getResources().getDisplayMetrics().scaledDensity);

    }

    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in {@link #TYPE_DIMENSION}.
     *
     * @param unit The unit to convert from.
     * @param value The value to apply the unit to.
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     *
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f/72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
    }
}
