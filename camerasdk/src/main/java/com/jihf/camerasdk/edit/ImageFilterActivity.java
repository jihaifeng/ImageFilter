package com.jihf.camerasdk.edit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jihf.camerasdk.BaseActivity;
import com.jihf.camerasdk.PhotoPickActivity;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.adapter.Filter_Effect_Adapter;
import com.jihf.camerasdk.adapter.Filter_Sticker_Adapter;
import com.jihf.camerasdk.adapter.FragmentViewPagerAdapter;
import com.jihf.camerasdk.adapter.SmallThumbAdapter;
import com.jihf.camerasdk.base.BaseCameraActivity;
import com.jihf.camerasdk.model.*;
import com.jihf.camerasdk.selector.ImageSelectProxy;
import com.jihf.camerasdk.ui.fragment.EfectFragment;
import com.jihf.camerasdk.utils.FilterUtils;
import com.jihf.camerasdk.view.CustomViewPager;
import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;
import com.muzhi.camerasdk.library.views.HorizontalListView;

import java.util.ArrayList;

public class ImageFilterActivity extends BaseCameraActivity {

    private CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();

    private HorizontalListView effect_listview, sticker_listview;

    private TextView tab_effect, tab_sticker, txt_cropper, btn_done, txt_enhance, txt_graffiti;
    private RelativeLayout loading_layout;// 等待框
    private SeekBar mSeekBar;


    private SmallThumbAdapter iAdapter;
    private Filter_Effect_Adapter eAdapter;
    private Filter_Sticker_Adapter sAdapter;

    private ArrayList<Filter_Effect_Info> effect_list = new ArrayList<>(); //特效
    private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<>();
    private ArrayList<String> imageList;

    public static Filter_Sticker_Info mSticker = null; // 从贴纸库过来的贴纸

    private FragmentViewPagerAdapter fAdapter;
    private CustomViewPager mViewPager;
    private ArrayList<Fragment> fragments;

    private int current = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.camerasdk_filter_image;
    }

    @Override
    protected void initViewAndEvent() {
        setTitle("图片编辑");
        setTvCameraNext("完成", new OnClickListener() {
            @Override
            public void onClick(View v) {
                complate();
            }
        });

        imageList = ImageEditProxy.getBuilder().getImageList();
        if (null != imageList) {
            initView();

            initEvent();

            initData();
        }


    }


    private void initView() {

        mViewPager = getView(R.id.viewpager);
        mViewPager.setPagingEnabled(true);

        tab_effect = getView(R.id.txt_effect);
        tab_sticker = getView(R.id.txt_sticker);
        txt_cropper = getView(R.id.txt_cropper);
        txt_enhance = getView(R.id.txt_enhance);
        txt_graffiti = getView(R.id.txt_graffiti);

        mSeekBar = getView(R.id.seekBar);
        effect_listview = getView(R.id.effect_listview);
        sticker_listview = getView(R.id.sticker_listview);
        loading_layout = getView(R.id.loading);

    }

    private void initEvent() {
        tab_effect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                effect_listview.setVisibility(View.VISIBLE);
                sticker_listview.setVisibility(View.INVISIBLE);
                tab_effect.setTextColor(getResources().getColor(R.color.camerasdk_txt_selected));
                tab_sticker.setTextColor(getResources().getColor(R.color.camerasdk_txt_normal));
            }
        });
        tab_sticker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                effect_listview.setVisibility(View.INVISIBLE);
                sticker_listview.setVisibility(View.VISIBLE);
                tab_effect.setTextColor(getResources().getColor(R.color.camerasdk_txt_normal));
                tab_sticker.setTextColor(getResources().getColor(R.color.camerasdk_txt_selected));
            }
        });
        txt_cropper.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 裁剪图片
                Constants.bitmap = ((EfectFragment) fragments.get(current)).getCurrentBitMap();
                Intent intent = new Intent();
                intent.setClassName(getApplication(), "com.jihf.camerasdk.CutActivity");
                startActivityForResult(intent, Constants.RequestCode_Croper);
            }
        });
        //        btn_done.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                // TODO 贴纸Tab
        //                complate();
        //            }
        //        });
        txt_enhance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO 图片增强
                Constants.bitmap = ((EfectFragment) fragments.get(current)).getCurrentBitMap();
                Intent intent = new Intent();
                intent.setClassName(getApplication(), "com.jihf.camerasdk.PhotoEnhanceActivity");
                startActivityForResult(intent, Constants.RequestCode_Croper);
            }
        });
        txt_graffiti.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO 涂鸦
                Constants.bitmap = ((EfectFragment) fragments.get(current)).getCurrentBitMap();
                Intent intent = new Intent();
                intent.putExtra("path", imageList.get(0));
                intent.setClassName(getApplication(), "com.jihf.camerasdk.GraffitiActivity");
                startActivityForResult(intent, Constants.RequestCode_Croper);
            }
        });

        effect_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                eAdapter.setSelectItem(arg2);

                final int tmpint = arg2;
                final int tmpitem = arg1.getWidth();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        effect_listview.scrollTo(tmpitem * (tmpint - 1) - tmpitem / 4);
                    }
                }, 200);

                Filter_Effect_Info info = effect_list.get(arg2);
                GPUImageFilter filter = ImageFilterTools.createFilterForType(mContext, info.getFilterType());
                ((EfectFragment) fragments.get(current)).addEffect(filter);
            }
        });

        sticker_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String path = stickerList.get(arg2).getLocal_path();
                int drawableId = stickerList.get(arg2).getDrawableId();
                ((EfectFragment) fragments.get(current)).addSticker(drawableId, path);

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragments.get(position).onResume();
                fragments.get(current).onPause();
                current = position;

                iAdapter.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });

    }

    private void initData() {

        boolean flag = false;
        if (mCameraSdkParameterInfo.isSingle_mode() && mCameraSdkParameterInfo.isCroper_image()) {
            flag = true;
        }

        fragments = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            EfectFragment ef1 = EfectFragment.newInstance(imageList.get(i), flag);
            fragments.add(ef1);
        }

        fAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);
        mViewPager.setAdapter(fAdapter);
        mViewPager.setCurrentItem(0);

        effect_list = FilterUtils.getEffectList();
        stickerList = FilterUtils.getStickerList();

        iAdapter = new SmallThumbAdapter(mContext, imageList);
        eAdapter = new Filter_Effect_Adapter(this, effect_list);
        sAdapter = new Filter_Sticker_Adapter(this, stickerList);

        iAdapter.setSelected(0);
        effect_listview.setAdapter(eAdapter);
        sticker_listview.setAdapter(sAdapter);

    }


    private void complate() {

        loading_layout.setVisibility(View.VISIBLE);
        complate_runnable(3 * 1000);

    }

    private void complate_runnable(long delayMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                ArrayList<String> list = new ArrayList<String>();
                //返回一个路径
                for (int i = 0; i < imageList.size(); i++) {
                    Fragment mFragment = fragments.get(i);
                    if (mFragment.isAdded()) {
                        String path = ((EfectFragment) fragments.get(i)).getFilterImage();
                        list.add(path);
                    } else {
                        list.add(imageList.get(i));
                    }
                }
                if (null != ImageEditProxy.getBuilder().getEditCallback()){
                    ImageEditProxy.getBuilder().getEditCallback().imageEditCallback(list);
                }
                finish();

            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, delayMillis);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.RequestCode_Croper) {
            //截图返回
            ((EfectFragment) fragments.get(current)).setBitMap();
        } else if (resultCode == Constants.RequestCode_Sticker) {
            if (data != null) {
                Filter_Sticker_Info info = (Filter_Sticker_Info) data.getSerializableExtra("info");
                ((EfectFragment) fragments.get(current)).addSticker(0, info.getImage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
