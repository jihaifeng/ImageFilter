package com.jihf.imagefilterdemo.viewPager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Func：重设Scroller的滑动速度
 * Desc:
 * Author：JHF
 * Date：2018/4/17 上午10:28
 * Mail：jihaifeng@meechao.com
 */
public class FixedSpeedScroller extends Scroller {
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    private int mDuration = 1000;


    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, Integer duration) {
        this(context, interpolator);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        this.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

}
