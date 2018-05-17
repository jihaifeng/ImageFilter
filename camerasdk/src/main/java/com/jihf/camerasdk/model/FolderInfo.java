package com.jihf.camerasdk.model;

import android.text.TextUtils;

import com.jihf.camerasdk.utils.ImageUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹
 */
public class FolderInfo implements Serializable{
    public String name;
    public String path;
    public ImageInfo cover;
    public ArrayList<ImageInfo> imageList = new ArrayList<>();

    public FolderInfo() {
    }

    public FolderInfo(String name) {
        this.name = name;
    }

    public FolderInfo(String name, ArrayList<ImageInfo> imageList) {
        this.name = name;
        if (null != imageList && imageList.size() > 0) {
            this.cover = imageList.get(0);
            this.path = ImageUtil.getFolderName(cover.path);
        }
        this.imageList = imageList;
    }


    public void addImage(ImageInfo image) {
        if (null != image && !TextUtils.isEmpty(image.path)) {
            if (null == imageList ) {
                imageList = new ArrayList<>();
            }
            imageList.add(image);
            cover = imageList.get(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        try {
            FolderInfo other = (FolderInfo) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
