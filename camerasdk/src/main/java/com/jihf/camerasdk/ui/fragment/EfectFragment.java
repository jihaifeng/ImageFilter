package com.jihf.camerasdk.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.model.Constants;
import com.jihf.camerasdk.utils.FileUtils;
import com.muzhi.camerasdk.library.filter.GPUImage;
import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.GPUImageView;
import com.muzhi.camerasdk.library.utils.PhotoUtils;
import com.muzhi.camerasdk.library.views.HSuperImageView;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class EfectFragment extends Fragment {

    private Context mContext;
    private View mView;
    private String mPath;
    private boolean mFlag = false; //true是正方形截图过来的
    private GPUImageView effect_main; // 需要修改的图像
    //public Bitmap sourceBitmap;	//当前的图片
    public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
    private int sticknum = -1;// 贴纸添加的序号


    public static EfectFragment newInstance(String path, boolean flag) {
        EfectFragment f = new EfectFragment();
        Bundle b = new Bundle();
        b.putString("path", path);
        b.putBoolean("flag", flag);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getArguments().getString("path");
        mFlag = getArguments().getBoolean("flag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.camerasdk_item_viewpage, container, false);
        effect_main = mView.findViewById(R.id.effect_main);
        sticklist = new ArrayList<>();
        effect_main.setScaleType(GPUImage.ScaleType.CENTER_CROP);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();


        initEvent();

        if (mFlag) {
            setSourceBitmap(Constants.bitmap);
        } else {
            if (mPath.startsWith("http://") || mPath.startsWith("https://")) {
                loadUrlImage();
            } else {
                effect_main.setImage(mPath);
            }
        }
    }

    private float getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private float getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    //加载网络图片
    private void loadUrlImage() {
        new Thread() {
            public void run() {
                try {
                    URL imageURl = new URL(mPath);
                    URLConnection con = imageURl.openConnection();
                    con.connect();
                    InputStream in = con.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();


                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setSourceBitmap(bitmap);
                        }

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    	/*FileUtils.doGetBitmap("http://img5.duitang.com/uploads/item/201511/28/20151128102233_cRxvN.jpeg", new Callback<Bitmap>() {

			@Override
			public void onSuccess(Bitmap obj) {
				// TODO Auto-generated method stub
				setSourceBitmap(obj);
			}
			
			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub
				
			}
		});*/
    }

    private void initEvent() {

        effect_main.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击屏幕背景   将所有贴纸的外框全部去掉贴
                hideStickEditMode();
            }
        });
    }

    /**
     * 改变图片(截图)
     */
    public void setBitMap() {
        setSourceBitmap(Constants.bitmap);
    }

    /**
     * 获取当前图片
     *
     * @return
     */
    public Bitmap getCurrentBitMap() {
        return effect_main.getCurrentBitMap();
    }

    //加特效
    public void addEffect(GPUImageFilter filter) {
        effect_main.setFilter(filter);
        effect_main.requestRender();
    }

    //加贴纸
    public void addSticker(int drawableId, final String path) {
        sticknum++;
        HSuperImageView imageView = new HSuperImageView(getActivity(), sticknum);

        if (drawableId > 0) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId);
            showSticker(bmp, imageView);
        } else {
            downLoad(drawableId, path, imageView);
        }

        sticklist.add(imageView);
        effect_main.addView(imageView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }


    private void showSticker(Bitmap bmp, HSuperImageView imageView) {
        if (bmp != null) {
            imageView.init(bmp);// 设置控件图片
            eventStickerImage(imageView);
            //将所有贴纸的外框全部去掉贴
            hideStickEditMode();
            imageView.setStickEditMode(true);
        } else {
            Toast.makeText(getActivity(), "加载贴纸失败", Toast.LENGTH_SHORT).show();
        }
    }


    //监听贴纸的事件
    private void eventStickerImage(HSuperImageView imageView) {

        imageView.setOnStickerListener(new HSuperImageView.OnStickerListener() {
            @Override
            public void onStickerModeChanged(int position, int flag, HSuperImageView view) {
                // TODO Auto-generated method stub
                if (flag == 1) {
                    //删除
                    try {
                        /*effect_main.removeView(sticklist.get(position));
                        sticklist.remove(position);*/
                        effect_main.removeView(view);
                        sticklist.remove(view);
                    } catch (Exception e) {
                    }
                } else if (flag == 2) {
                    //点击
                    hideStickEditMode();
                    view.setStickEditMode(true);
                    /*boolean mode=sticklist.get(position).getStickEditMode();
                    sticklist.get(position).setStickEditMode(!mode);
					sticklist.get(position).invalidate();*/
                }
            }
        });
    }

    //将所有的贴纸修改成不可编辑的模式(外框全部去掉贴)
    private void hideStickEditMode() {
        for (int i = 0; i < sticklist.size(); i++) {
            sticklist.get(i).setStickEditMode(false);
            sticklist.get(i).invalidate();
        }
    }


    //获取最终的图片的路径
    public String getFilterImage() {

        effect_main.setDrawingCacheEnabled(true);
        Bitmap editbmp = Bitmap.createBitmap(effect_main.getDrawingCache());

        try {
            Bitmap fBitmap = effect_main.capture();
            Bitmap bitmap = Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(), Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(fBitmap, 0, 0, null);
            cv.drawBitmap(editbmp, 0, 0, null);

            //最终合并生成图片
            String path = FileUtils.saveBitmap(mContext, bitmap);
            bitmap.recycle();
            return path;

        } catch (Exception e) {
            return "";
        }
    }

    //获取最终的图片的Bitmap
    public Bitmap getFilterBitmap() {

        effect_main.setDrawingCacheEnabled(true);
        Bitmap editbmp = Bitmap.createBitmap(effect_main.getDrawingCache());

        try {
            Bitmap fBitmap = effect_main.capture();
            Bitmap bitmap = Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(), Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(fBitmap, 0, 0, null);
            cv.drawBitmap(editbmp, 0, 0, null);

            fBitmap.recycle();
            editbmp.recycle();

            return bitmap;

        } catch (Exception e) {
            return null;
        }
    }

    protected final int GETSTICKER_SUCC = 0;
    protected final int DOWNLOADSTICKER_SUCC = 1;

    //下载图片
    private void downLoad(final int drawableId, final String path, final HSuperImageView imageView) {
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Constants.bitmap = BitmapFactory.decodeStream(is);
                    is.close();

                } catch (Exception e) {
                }

                Message msg = new Message();
                msg.what = DOWNLOADSTICKER_SUCC;
                msg.arg1 = drawableId;
                msg.obj = imageView;
                handler.sendMessage(msg);
            }
        }.start();
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOADSTICKER_SUCC:
                    showSticker(Constants.bitmap, (HSuperImageView) msg.obj);
                    break;
            }
        }
    };


    /**
     * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片
     *
     * @param bitmap 源bitmap
     * @param w      缩放后指定的宽度
     * @param h      缩放后指定的高度
     * @return 缩放后的中间部分图片 Bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("TAG", "zoomBitmap---" + "width:" + width + "---" + "height:" + height);
        float scaleWidth, scaleHeight, x, y;
        Bitmap newBmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidth = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;// 获取bitmap源文件中x坐标需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y坐标需要偏移的像数大小
        } else {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            newBmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x), (int) (height - y), matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newBmp;
    }


    private void setSourceBitmap(Bitmap sourceBitmap) {
        float width = sourceBitmap.getWidth();
        float height = sourceBitmap.getHeight();
        Log.i("setSourceBitmap", "setSourceBitmap: " + width + "  " + height);
        float ratio = width / height;

        effect_main.setRatio(ratio);
        effect_main.setImage(sourceBitmap);
    }

}
