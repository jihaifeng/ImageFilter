package com.jihf.camerasdk.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.jihf.camerasdk.model.FolderInfo;
import com.jihf.camerasdk.model.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/16 下午2:16
 * Mail：jihaifeng@meechao.com
 */
public class ImageUtil {

    /**
     * 从SDCard加载图片
     *
     * @param context
     * @param callback
     */
    public static void loadImageForSDCard(final Context context, final DataCallback callback) {
        //由于扫描图片是耗时的操作，所以要在子线程处理。
        new Thread(new Runnable() {
            @Override
            public void run() {
                //扫描图片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = context.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID}, null, null, MediaStore.Images.Media.DATE_ADDED);

                ArrayList<ImageInfo> images = new ArrayList<>();

                //读取扫描到的图片
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //获取图片名称
                        String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //获取图片时间
                        long time = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        if (!".downloading".equals(getExtensionName(path))) { //过滤未下载完成的文件
                            images.add(new ImageInfo(path, name, time));
                        }
                    }
                    mCursor.close();
                }
                Collections.reverse(images);
                callback.onSuccess(splitFolder(images));
            }
        }).start();
    }

    /**
     * 把图片按文件夹拆分，第一个文件夹保存所有的图片
     *
     * @param images
     * @return
     */
    private static ArrayList<FolderInfo> splitFolder(ArrayList<ImageInfo> images) {

        ArrayList<FolderInfo> folders = new ArrayList<>();
        folders.add(new FolderInfo("全部图片", images));

        if (images != null && !images.isEmpty()) {
            for (ImageInfo imageInfo:images) {
                String path = imageInfo.path;
                String name = getFolderName(path);
                if (!TextUtils.isEmpty(name)) {
                    FolderInfo _folderInfo = getFolder(name, folders);
                    _folderInfo.addImage(imageInfo);
                }
            }
        }
        return folders;
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 根据图片路径，获取图片文件夹名称
     *
     * @param path
     * @return
     */
    public static String getFolderName(String path) {
        if (!TextUtils.isEmpty(path)) {
            String[] strings = path.split(File.separator);
            if (strings.length >= 2) {
                return strings[strings.length - 2];
            }
        }
        return "";
    }

    private static FolderInfo getFolder(String name, List<FolderInfo> folders) {
        if (!folders.isEmpty()) {
            int size = folders.size();
            for (int i = 0; i < size; i++) {
                FolderInfo folder = folders.get(i);
                if (name.equals(folder.name)) {
                    return folder;
                }
            }
        }
        FolderInfo newFolder = new FolderInfo(name);
        folders.add(newFolder);
        return newFolder;
    }

    public interface DataCallback {
        void onSuccess(ArrayList<FolderInfo> folders);
    }

}
