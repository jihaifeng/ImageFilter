package com.jihf.imagefilterdemo.viewPager;

import android.widget.ImageView;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/4/17 上午10:32
 * Mail：jihaifeng@meechao.com
 */
public interface SmartVPInterface {

    /**
     * 图片加载
     *
     * @param imageView view
     * @param url       url
     * @param position  pos
     */
    void onLoadImage(ImageView imageView, String url, int position);

    /**
     * 第一页加载结束后 回调
     *
     * @param height 高度
     */
    void firstPicLoadFinished(int height);

    /**
     * 点击事件
     *
     * @param position pos
     */
    void onItemClick(int position);

    /**
     * 页面滑动回调
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int curHeight);

    /**
     * 当前选中第几页
     *
     * @param position pos
     */
    void onPageSelected(int position);

    void onDefaultSelect(int position);


    /**
     * 滑动状态改变
     *
     * @param state 状态
     */
    void onPageScrollStateChanged(int state);
}
