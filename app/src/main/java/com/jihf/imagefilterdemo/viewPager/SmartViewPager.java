package com.jihf.imagefilterdemo.viewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Func：自定义viewpager实现滑动时 每张高度不同的图片 能够平滑过渡
 * Desc:
 * Author：JHF
 * Date：2018/4/17 上午10:42
 * Mail：jihaifeng@meechao.com
 */
public class SmartViewPager extends ViewPager {

    protected int fixedSpeed;//重设滚动的速度
    protected int[] sourceHeights;//每张图片的高度
    private float defaultHeight;

    protected SmartVpListener vpListener;//Mu5ViewPager涉及到的一切事件回调
    protected SmartVPAdapter pagerAdapter;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private int defaultSelectPos;

    /**
     * Mu5ViewPager涉及到的一切事件回调
     *
     * @param vpListener vpInterface
     */
    public void setVpInterface(SmartVpListener vpListener) {
        this.vpListener = vpListener;
        if (pagerAdapter != null) {
            pagerAdapter.setVpInterface(vpListener);
        }
    }

    protected OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == sourceHeights.length - 1 || defaultHeight == 0) {//用于defaultHeight初始化之前的拦截,即等到获取到第一张图片再做操作
                return;
            }
            //计算ViewPager即将变化到的高度
            int height = (int) ((sourceHeights[position] == 0 ? defaultHeight : sourceHeights[position]) * (1 - positionOffset) + (sourceHeights[(position + 1)] == 0 ? defaultHeight : sourceHeights[(position + 1)]) * positionOffset);

            //为ViewPager设置高度
            ViewGroup.LayoutParams params = SmartViewPager.this.getLayoutParams();
            params.height = height;
            SmartViewPager.this.setLayoutParams(params);

            if (vpListener != null) {
                vpListener.onPageScrolled(position, positionOffset, positionOffsetPixels, height);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (vpListener != null) {
                vpListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (vpListener != null) {
                vpListener.onPageScrollStateChanged(state);
            }
        }
    };

    public SmartViewPager(Context context) {
        this(context, null);
    }

    public SmartViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        if (fixedSpeed > 0) {
            setScrollerSpeed(fixedSpeed);
        }
        addOnPageChangeListener(onPageChangeListener);
        setOffscreenPageLimit(15);
    }

    /**
     * 设置图片的网络链接
     *
     * @param urls url
     */
    public void setNewData(List<String> urls) {
        if (urls == null || urls.size() == 0)
            throw new RuntimeException("error:don't give a empty source to me!");
        removeAllViews();
        sourceHeights = new int[urls.size()];
        if (pagerAdapter != null) {
            pagerAdapter.setUrls(urls);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置图片的网络链接，回调接口（注意：这个必须实现的）
     *
     * @param urls       url
     * @param vpListener vpListener
     */
    public void setData(List<String> urls, SmartVpListener vpListener) {
        setData(urls, 0, vpListener);
    }

    /**
     * 设置图片的网络链接，回调接口（注意：这个必须实现的）
     *
     * @param urls       url
     * @param vpListener vpListener
     */
    public void setData(List<String> urls, int selectPos, SmartVpListener vpListener) {
        if (urls == null || urls.size() == 0)
            throw new RuntimeException("error:don't give a empty source to me!");
        this.vpListener = vpListener;
        sourceHeights = new int[urls.size()];
        pagerAdapter = new SmartVPAdapter(getContext(), urls, vpListener);
        this.defaultSelectPos = selectPos;
        this.setAdapter(pagerAdapter);
    }


    public int[] getSourceHeights() {
        return sourceHeights;
    }

    /**
     * 设置图片的网络链接，回调接口（注意：这个必须实现的）
     *
     * @param urls       url
     * @param vpListener vpListener
     */
    public void setData(String[] urls, SmartVpListener vpListener) {
        setData(Arrays.asList(urls), vpListener);
    }


    public void bindSource(Bitmap loadedImage, int position, ImageView imageView) {
        if (loadedImage != null) {
            int height = sourceHeights[position];
            if (height <= 0) {
                float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
                height = (int) (scale * ScreenUtil.getScreenWidth());
                height = (int) Math.min(height, ScreenUtil.getScreenHeight() * 3 / 5);
                height = (int) Math.max(height, ScreenUtil.getScreenHeight() * 3 / 10);
            }
            setSourceHeights(height, position);
            Bitmap _bm = BitmapUtil.zoomBitmap(loadedImage, (int) ScreenUtil.getScreenWidth(), height);
            if (null != _bm) {
                imageView.setImageBitmap(_bm);
            }
            if (position == 0 && null != vpListener) {
                vpListener.firstPicLoadFinished(sourceHeights[0]);
            }
            if (null != vpListener) {
                vpListener.onDefaultSelect(defaultSelectPos);
            }
        } else {
//            CameraToast.makeText(getContext(), "error:picture is empty", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 设置加载出的图片的高度参数
     * <p>
     * 设计目的：主要便于用户使用自己的加载框架
     *
     * @param height   h
     * @param position pos
     */
    private void setSourceHeights(int height, int position) {

        if (height < 0) {
            throw new RuntimeException("error:i got a wrong height:" + height);
        }

        if (sourceHeights == null || sourceHeights.length == 0 || sourceHeights.length <= position) {
            throw new RuntimeException("error:i don't have so much more index");
        }

        sourceHeights[position] = height;
        if (position == 0 && defaultHeight == 0) {//初始化默认高度
            defaultHeight = height;
            ViewGroup.LayoutParams params = SmartViewPager.this.getLayoutParams();
            params.height = height;
            SmartViewPager.this.setLayoutParams(params);
        }
    }

    public static String toString(int[] arr) {
        //1,定义字符串变量。
        String temp = "[";
        //2,遍历数组。将每一个数组的元素和字符串相连接。
        for (int x = 0; x < arr.length; x++) {
            //判断，不是最后一个元素，后面连接逗号，是最后一个元素，后面不连接逗号。
            if (x != arr.length - 1)
                temp = temp + arr[x] + ",";
            else
                temp = temp + arr[x] + "]";
        }


        //3，将连接后的字符串返回。哦耶！
        return temp;
    }


    public void setScrollerSpeed(int speed) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), null, speed);
            mScroller.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void removeByPosition(int position) {
        if (position - 1 >= 0 && position == getChildCount() - 1) {
            setCurrentItem(position - 1);
        } else if (position < getChildCount()) {
            setCurrentItem(position);
        }
        pagerAdapter.removeByPosition(position);
    }

    public ImageView getCurImageView(int pos) {
        if (null != pagerAdapter && null != pagerAdapter.getImageMap() && pagerAdapter.getImageMap().containsKey(pos)) {
            return pagerAdapter.getImageMap().get(pos);
        }
        return null;
    }

    public SmartVPAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public Bitmap getBitmapForPosition(int pos) {
        return bitmaps.get(pos);
    }
}

