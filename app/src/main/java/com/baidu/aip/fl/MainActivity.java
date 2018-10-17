/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.fl;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.baidu.aip.fl.utils.Constant;
import com.baidu.aip.fl.utils.HttpUtil;
import com.baidu.aip.fl.utils.Msg;
import com.baidu.aip.fl.utils.PreferencesUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.baidu.aip.fl.widget.LoadingDialog;
import com.hearing.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button regBtn;
    private Button login1Btn;
    private Button resultBtn;
    private Button startBtn;
    private Button detectedBtn;
    private Button resetBtn;
    private SharedPreferences sharedPreferences;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);

        findView();
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findView() {
        regBtn = (Button) findViewById(R.id.reg_btn);
        resultBtn = (Button) findViewById(R.id.result_btn);
        startBtn = (Button) findViewById(R.id.start_btn);
        login1Btn = (Button) findViewById(R.id.login1_btn);
        detectedBtn = (Button) findViewById(R.id.detected_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
    }

    private void addListener() {
        regBtn.setOnClickListener(this);
        resultBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        login1Btn.setOnClickListener(this);
        detectedBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    /**
     * 显示加载的进度款
     */
    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, "开启中",
                    false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    private void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.dismiss();
                }
            });

        }
    }

    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        if (regBtn == v) {
            Intent intent = new Intent(MainActivity.this, RegActivity.class);
            startActivity(intent);
        } else if (login1Btn == v) {
            // TODO 实时人脸检测
            Intent intent = new Intent(MainActivity.this, DetectLoginActivity.class);
            startActivity(intent);
        } else if (detectedBtn == v) {
            Intent intent = new Intent(MainActivity.this, VerifyLoginActivity.class);
            startActivity(intent);
        } else if (resetBtn == v) {
            PreferencesUtil.initPrefs(getApplicationContext());
            PreferencesUtil.remove("username");
        } else if (resultBtn == v) {
            startActivity(new Intent(MainActivity.this, ResultActivity.class));
        } else if (startBtn == v) {

            showLoading();

            HashMap<String, String> headers = new HashMap<>();
            headers.put(Constant.LOGIN_TOKEN,
                    sharedPreferences.getString(Constant.LOGIN_TOKEN, ""));
            HttpUtil.getInstance().getAsyn("aiface/clear/", null,
                    headers, new ReqCallBack<Msg>() {
                        @Override
                        public void onReqSuccess(Msg result) {
                            hideLoading();

                            switch (result.getCode()) {
                                case Constant.CODE_SUCCESS:
                                    Toast.makeText(MainActivity.this,
                                            "开启签到成功!", Toast.LENGTH_LONG).show();
                                    break;
                                case Constant.CODE_FAILED:
                                    Toast.makeText(MainActivity.this,
                                            "服务器错误!", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            hideLoading();
                            Toast.makeText(MainActivity.this,
                                    "服务器错误!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
