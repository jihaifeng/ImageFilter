package com.jihf.imagefilterdemo.camerasdk.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jihf.imagefilterdemo.R;

import java.util.ArrayList;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018/5/17 下午6:02
 * Mail：jihaifeng@meechao.com
 */
public class ResultActivityTwo extends FragmentActivity{
    private RecyclerView rcvImage;
    private ResultAdapterTwo resultAdapter;
    public static void start(Activity activity, ArrayList<String> arrayList) {
        Intent intent = new Intent(activity, ResultActivityTwo.class);
        intent.putStringArrayListExtra("a", arrayList);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_t);
        initViewAndEvent();
    }

    protected void initViewAndEvent() {
        rcvImage = findViewById(R.id.rcv_camera_image);

        resultAdapter = new ResultAdapterTwo(this);

        rcvImage.setHasFixedSize(true);
        rcvImage.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rcvImage.setLayoutManager(new GridLayoutManager(this, 4));
        rcvImage.setAdapter(resultAdapter);

        resultAdapter.refresh(getIntent().getStringArrayListExtra("a"));
    }
}
