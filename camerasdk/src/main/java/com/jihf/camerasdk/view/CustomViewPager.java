package com.jihf.camerasdk.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * viewpager
 * jazzy
 */
public class CustomViewPager extends ViewPager {

	private boolean enabled=true;//false;//默认不可滑动


    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.enabled = true;
    }

    //触摸没有反应就可以了
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
        	 try {  
                 return super.onTouchEvent(event);  
             } catch (IllegalArgumentException ex) {
                 ex.printStackTrace();  
             }  
        }
  
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
        	try {  
                return super.onInterceptTouchEvent(event);  
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();  
            } 
        }
        
        return false;
    }
 
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);//<span style="color: rgb(70, 70, 70); font-family: 'Microsoft YaHei', 'Helvetica Neue', SimSun; font-size: 14px; line-height: 21px; background-color: rgb(188, 211, 229);">表示切换的时候，不需要切换时间。</span>
    }
}
