package com.jihf.camerasdk.selector;

import com.jihf.camerasdk.model.ImageInfo;

import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/17 下午3:52
 * Mail：jihaifeng@meechao.com
 */
public interface ImageSelectCallback {
    void imageCallBack(ArrayList<ImageInfo> imageList);
}
