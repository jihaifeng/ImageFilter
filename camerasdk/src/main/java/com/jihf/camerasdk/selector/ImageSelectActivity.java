package com.jihf.camerasdk.selector;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jihf.camerasdk.PhotoPickActivity;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.base.BaseCameraActivity;
import com.jihf.camerasdk.model.CameraSdkParameterInfo;
import com.jihf.camerasdk.model.FolderInfo;
import com.jihf.camerasdk.model.ImageInfo;
import com.jihf.camerasdk.selector.adapter.CameraFolderAdapter;
import com.jihf.camerasdk.selector.adapter.CameraImageAdapter;
import com.jihf.camerasdk.utils.FileUtils;
import com.jihf.camerasdk.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/16 上午11:24
 * Mail：jihaifeng@meechao.com
 */
public class ImageSelectActivity extends BaseCameraActivity {

    private ArrayList<FolderInfo> folderList;
    private ImageSelectProxy.Builder builder;

    private RecyclerView rcvCameraImage;
    private TextView tvTitle;

    private CameraImageAdapter imageAdapter;
    private CameraFolderAdapter folderAdapter;
    private PopupWindow folderPopup;

    private FolderInfo curFolder;

    private ImageInfo cameraPlaceHolder;
    public static String CAMERA_PLACE_HOLDER = "camera_place_holder_path";

    private static int CAMERA_TAKE_PHOTO = 100;

    private File mTmpFile = null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_select;
    }

    @Override
    protected void initViewAndEvent() {

        builder = ImageSelectProxy.getBuilder();
        if (null == builder) {
            return;
        }

        rcvCameraImage = getView(R.id.rcv_camera_image);
        tvTitle = getTvCameraTitle();

        imageAdapter = new CameraImageAdapter(this, builder.getMaxCount());

        rcvCameraImage.setHasFixedSize(true);
        rcvCameraImage.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rcvCameraImage.setLayoutManager(new GridLayoutManager(this, builder.getGridRowCount()));
        rcvCameraImage.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new CameraImageAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(ImageInfo image, int position) {
                showToast("click");
                if (image.name.equalsIgnoreCase(CAMERA_PLACE_HOLDER)) {
                    showToast("相机");
                    requestPermission(new String[]{Manifest.permission.CAMERA}, CODE_CAMERA_PERMISSION);
                } else {
                    showToast("preView");
                }
            }
        });

        imageAdapter.setOnImageSelectListener(new CameraImageAdapter.OnImageSelectListener() {
            @Override
            public void OnImageSelect(ImageInfo image, boolean isSelect, int selectCount) {
                setSelectImageCount(selectCount);
            }
        });

        setSelectImageCount(imageAdapter.getSelectImages().size());

        cameraPlaceHolder = new ImageInfo(CAMERA_PLACE_HOLDER, CAMERA_PLACE_HOLDER, 0);

        initFolderPopup();

        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_STORAGE_PERMISSION);

    }


    /**
     * Open camera
     */
    private void showCameraAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            try {
                mTmpFile = FileUtils.createTmpFile(ImageSelectActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                     /*获取当前系统的android版本号*/
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                Log.e("currentapiVersion", "currentapiVersion====>" + currentapiVersion);
                if (currentapiVersion < 24) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                    startActivityForResult(intent, CAMERA_TAKE_PHOTO);
                } else {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAMERA_TAKE_PHOTO);
                }
            } else {
                Toast.makeText(this, "图片错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "没有系统相机", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFolderPopup() {
        folderAdapter = new CameraFolderAdapter(this);

        View view = getLayoutInflater().inflate(R.layout.camera_folder_popup, null);
        LinearLayout llPopup = view.findViewById(R.id.ll_popup);
        llPopup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.camerasdk_push_up_in));

        RecyclerView rcvFolder = view.findViewById(R.id.rcv_folder);
        rcvFolder.setHasFixedSize(true);
        rcvFolder.setLayoutManager(new LinearLayoutManager(ImageSelectActivity.this));
        rcvFolder.setAdapter(folderAdapter);

        if (folderPopup == null) {
            folderPopup = new PopupWindow(mContext);
            folderPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            folderPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            folderPopup.setFocusable(true);
            folderPopup.setOutsideTouchable(true);
        }

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                folderPopup.dismiss();
            }
        });

        folderAdapter.setOnFolderSelectListener(new CameraFolderAdapter.OnFolderSelectListener() {
            @Override
            public void OnFolderSelect(FolderInfo folder) {
                curFolder = folder;
                updateImageData();
                folderPopup.dismiss();
            }
        });


        folderPopup.setContentView(view);
        folderPopup.setBackgroundDrawable(new ColorDrawable(0xb0000000));
    }

    private void setSelectImageCount(int count) {
        if (builder.getMaxCount() != 1) {
            if (builder.getMaxCount() > 0) {
                setTvCameraNext(String.format("确定(%s/%s)", count, builder.getMaxCount()), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doNext();
                    }
                });
            } else {
                setTvCameraNext(String.format("确定(%s)", count), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doNext();
                    }
                });
            }
        }
    }

    private void doNext() {
        showToast("next");
    }

    @Override
    public void permissionFail(int requestCode) {
        super.permissionFail(requestCode);
        if (requestCode == CODE_STORAGE_PERMISSION) {
            showToast("缺少存储权限");
        } else if (requestCode == CODE_CAMERA_PERMISSION) {
            showToast("缺少相机权限");
        }
    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        if (requestCode == CODE_STORAGE_PERMISSION) {
            ImageUtil.loadImageForSDCard(this, new ImageUtil.DataCallback() {
                @Override
                public void onSuccess(ArrayList<FolderInfo> folders) {
                    folderList = folders;
                    curFolder = folderList.get(0);
                    setImageData();
                }
            });
        } else if (requestCode == CODE_CAMERA_PERMISSION) {
            showCameraAction();
        }
    }

    private void setImageData() {
        if (null == folderList || folderList.size() <= 0) {
            showToast("没有图片");
            return;
        }

        folderAdapter.refresh(folderList);

        setTitleData();

        imageAdapter.refresh(formatImageData(curFolder.imageList));

        imageAdapter.setSelectedImages(imageAdapter.getSelectImages());

    }

    private void updateImageData() {
        if (null == curFolder) {
            showToast("该文件夹下没有图片");
            return;
        }

        setTitleData();

        imageAdapter.refresh(formatImageData(curFolder.imageList));

    }

    private ArrayList<ImageInfo> formatImageData(ArrayList<ImageInfo> imageList) {
        imageList.remove(cameraPlaceHolder);
        if (builder.needCamera()) {
            imageList.add(0, cameraPlaceHolder);
        }

        return imageList;

    }

    private void setTitleData() {
        if (null == curFolder) {
            tvTitle.setText("图片");
            return;
        }
        tvTitle.setText(curFolder.name);
        Drawable drawable = getResources().getDrawable(R.drawable.message_popover_arrow);
        drawable.setBounds(10, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvTitle.setCompoundDrawables(null, null, drawable, null);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderPopup.showAsDropDown(getRlCameraBaseToolbar());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if (requestCode == CAMERA_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {

                    //加入content provider
                    ContentValues values = new ContentValues(7);
                    values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, "");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.ORIENTATION, 0);
                    values.put(MediaStore.Images.Media.DATA, mTmpFile.getPath());
                    values.put(MediaStore.Images.Media.SIZE, mTmpFile.length());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    if (builder.getMaxCount() == 1) {
                        //                        resultList.clear();
                        //                        resultList.add(mTmpFile.getPath());
                        //                        selectComplate();
                    } else {
                        imageAdapter.getSelectImages().add(new ImageInfo(mTmpFile.getPath(), mTmpFile.getName(), System.currentTimeMillis()));
                        imageAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    mTmpFile.delete();
                }
            }
        }
    }

}
