package com.jihf.imagefilterdemo.viewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-11-11-0011 16:38
 * Mail：jihaifeng@meechao.com
 */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName().trim();
    private static final String SD_PATH = "/sdcard/Meehe_Cache/";
    private static final String IN_PATH = "/Meehe_Cache/";

    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     * @return
     */
//    public static String saveBitmap(Bitmap mBitmap, String imageName) {
//        String savePath;
//        File filePic;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            savePath = SD_PATH;
//        } else {
//            savePath = App.getInstance().getApplicationContext().getApplicationContext().getFilesDir().getAbsolutePath() + IN_PATH;
//        }
//        try {
//            filePic = new File(savePath + imageName + ".jpg");
//            if (!filePic.exists()) {
//                filePic.getParentFile().mkdirs();
//                filePic.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(filePic);
//            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//
//        return filePic.getAbsolutePath();
//    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @param maxkb
     * @return b
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        int i;
        int j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
            bitmap.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmap(String url) {

        URL fileUrl = null;
        Bitmap bitmap = null;
        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (OutOfMemoryError error) {
            Log.i("error", "error：" + error);
        } catch (Exception e) {
            Log.e("e：", "e： " + e);
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void covBitmapFromUrl(String url, int maxkb, covLinstener linstener) {
        if (TextUtils.isEmpty(url)) {
            linstener.covFailure();
            return;
        }
        try {
            new Thread(() -> {
                long _time = System.currentTimeMillis();
                Log.i(TAG, "covBitmapFromUrl: " + _time);
                Bitmap _temp = getBitmap(url);

                Log.i(TAG, "covBitmapFromUrl1: " + (System.currentTimeMillis() - _time));
                _time = System.currentTimeMillis();

                byte[] bitmapByte = bitmap2Bytes(_temp, maxkb);
                Log.i(TAG, "covBitmapFromUrl2: " + (System.currentTimeMillis() - _time));
                _time = System.currentTimeMillis();
                linstener.covSuccess(bitmapByte);
            }).start();
        } catch (Exception e) {
            linstener.covFailure();
        }
    }

    public interface covLinstener {
        void covSuccess(byte[] bitmapByte);

        void covFailure();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

//    public static Bitmap getBlurBitmap(String url, float radius) {
//        Bitmap _bitmap = getBitmap(url);
//        return getBlurBitmap(_bitmap, radius);
//    }

//    public static Bitmap getBlurBitmap(Bitmap bitmap, float radius) {
//        Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
//        RenderScript rs = RenderScript.create(App.getInstance().getApplicationContext()); // 构建一个RenderScript对象
//        ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); //
//        // 创建高斯模糊脚本
//        Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 开辟输入内存
//        Allocation allOut = Allocation.createFromBitmap(rs, output); // 开辟输出内存
//        gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
//        gaussianBlue.setInput(allIn); // 设置输入内存
//        gaussianBlue.forEach(allOut); // 模糊编码，并将内存填入输出内存
//        allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
//        rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
//        return output;
//    }

    /**
     * 图片缩放到铺满屏幕宽度
     *
     * @param bitmap 源bitmap
     * @return 缩放后的图片 Bitmap
     */
    public static Bitmap zoomBitmapFitScreenWidth(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("TAG", "zoomBitmap---" + "width:" + width + "---" + "height:" + height);
        float scaleWidth, scaleHeight, x, y;
        Bitmap newBmp;
        Matrix matrix = new Matrix();

        scaleWidth = getInitImageScale(bitmap);
        scaleHeight = getInitImageScale(bitmap);
        x = 0;
        y = 0;

        matrix.postScale(scaleWidth, scaleHeight);
        try {
            newBmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x), (int) (height - y), matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newBmp;
    }

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

    public static Bitmap getBitmapFromPath(String imagePath) {
        try {
            return BitmapFactory.decodeFile(imagePath);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.i(TAG, "getBitmapFromPath: " + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 计算出图片初次显示需要放大倍数
     *
     * @param imagePath 图片的绝对路径
     * @return 放大的倍数
     */
    public static float getInitImageScale(String imagePath) {
        Bitmap bitmap = getBitmapFromPath(imagePath);
        return getInitImageScale(bitmap);
    }


    /**
     * 计算出图片初次显示需要放大倍数
     *
     * @param bitmap 图片
     * @return 放大的倍数
     */
    public static float getInitImageScale(Bitmap bitmap) {
        int width = (int) ScreenUtil.getScreenWidth();
        int height = (int) ScreenUtil.getScreenHeight();
        return getInitImageScale(bitmap, width, height);
    }

    /**
     * 计算出图片初次显示需要放大倍数
     *
     * @param bitmap 图片
     * @return 放大的倍数
     */
    public static float getInitImageScale(Bitmap bitmap, int width, int height) {
        if (null == bitmap) {
            return 1f;
        }
        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();
        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        return scale;
    }
}
