package com.jihf.camerasdk.selector;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jihf.camerasdk.R;
import com.jihf.camerasdk.base.BaseCameraActivity;
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
    private static final String TAG = ImageSelectActivity.class.getSimpleName().trim();
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
                if (image.name.equalsIgnoreCase(CAMERA_PLACE_HOLDER)) {
                    requestPermission(new String[]{Manifest.permission.CAMERA}, CODE_CAMERA_PERMISSION);
                } else {
                    if (builder.getMaxCount() == 1) {
                        selectComplete();
                    } else {
                        showToast("preView");
                    }
                }
            }
        });

        imageAdapter.setOnImageSelectListener(new CameraImageAdapter.OnImageSelectListener() {
            @Override
            public void OnImageSelect(ImageInfo image, boolean isSelect, int selectCount) {
                setSelectImageCount(selectCount);
            }
        });

        //        imageAdapter.setSelectedImages(builder.getSelectList());

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
                        selectComplete();
                    }
                });
            } else {
                setTvCameraNext(String.format("确定(%s)", count), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectComplete();
                    }
                });
            }
        }
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
        try {
            if (null == folderList || folderList.size() <= 0) {
                showToast("没有图片");
                return;
            }

            folderAdapter.refresh(folderList);

            setTitleData();

            imageAdapter.refresh(formatImageData(curFolder.imageList));

            imageAdapter.setSelectedImages(builder.getSelectList());
        } catch (Exception e) {
            e.printStackTrace();
        }


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


                    String folderName = ImageUtil.getFolderName(mTmpFile.getPath());
                    boolean containsFlag = false;
                    FolderInfo _folder = null;

                    FolderInfo allImageFolder = null;
                    for (FolderInfo _f : folderList) {
                        Log.i(TAG, "onActivityResult: " + _f.name);
                        Log.i(TAG, "onActivityResult: " + folderName);
                        if (_f.name.equalsIgnoreCase("全部图片")) {
                            allImageFolder = _f;
                        }
                        if (_f.name.equalsIgnoreCase(folderName)) {
                            containsFlag = true;
                            _folder = _f;
                        }
                    }
                    if (!containsFlag) {
                        _folder = new FolderInfo(folderName);
                        folderList.add(_folder);
                        folderAdapter.notifyDataSetChanged();
                    }

                    ImageInfo imageInfo = new ImageInfo(mTmpFile.getPath(), mTmpFile.getName(), System.currentTimeMillis());
                    _folder.imageList.add(0, imageInfo);


                    if (null != allImageFolder) {
                        allImageFolder.imageList.add(1, imageInfo);
                        imageAdapter.notifyDataSetChanged();
                    }

                    if (builder.getMaxCount() == 1) {
                        selectComplete();
                    } else {
                        imageAdapter.getSelectImages().add(imageInfo);
                        imageAdapter.notifyDataSetChanged();
                        setSelectImageCount(imageAdapter.getSelectImages().size());

                    }
                }
            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    FileUtils.deleteFile(this, mTmpFile);
                }
            }
        }
    }


    //选择完成实现跳转
    private void selectComplete() {
        if (null != builder.getCallback()) {
            builder.getCallback().imageCallBack(imageAdapter.getSelectImages());
        }
        this.finish();

        //        Bundle b = new Bundle();
        //        b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
        //
        //        Intent intent = new Intent();
        //        intent.putExtras(b);

        //        if (builder.getMaxCount() == 1) {
        //            //单选模式
        //            if (mCameraSdkParameterInfo.isCroper_image()) {
        //                //跳转到图片裁剪
        //                intent = new Intent(this, CropperImageActivity.class);
        //                intent.putExtras(b);
        //                startActivity(intent);
        //            } else if (mCameraSdkParameterInfo.isFilter_image()) {
        //                //跳转到滤镜
        //                intent = new Intent(this, FilterImageActivity.class);
        //                intent.putExtras(b);
        //                startActivity(intent);
        //            } else {
        //                setResult(RESULT_OK, intent);
        //                finish();
        //            }
        //        } else {
        //            //多选模式
        //            if (mCameraSdkParameterInfo.isFilter_image()) {
        //                //跳转到滤镜
        //                intent = new Intent(this, FilterImageActivity.class);
        //                intent.putExtras(b);
        //                startActivity(intent);
        //            } else {
        //                setResult(RESULT_OK, intent);
        //                finish();
        //            }
        //        }

    }

}
