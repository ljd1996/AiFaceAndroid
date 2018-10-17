/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.fl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import android.widget.Toast;
import com.baidu.aip.fl.utils.Constant;
import com.baidu.aip.fl.utils.HttpUtil;
import com.baidu.aip.fl.utils.ImageSaveUtil;
import com.baidu.aip.fl.utils.Msg;
import com.hearing.R;

import java.util.HashMap;


/**
 * 登陆结果页面
 */

public class LoginResultActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 100;
    private TextView idTv;
    private TextView nameTv;
    private TextView groupTv;
    private Button backBtn;
    private ImageView headIv;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_result);

        sharedPreferences = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);

        findView();
        addListener();
        displayData();

    }

    private void findView() {
        idTv = (TextView) findViewById(R.id.id_tv);
        nameTv = (TextView) findViewById(R.id.name_tv);
        groupTv = (TextView) findViewById(R.id.group_tv);
        backBtn = (Button) findViewById(R.id.back_btn);
        headIv = (ImageView) findViewById(R.id.head_iv);
    }

    private void displayData() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean loginSuccess = intent.getBooleanExtra("login_success", false);

            if (loginSuccess) {
//                idTv.setText("识别成功");
                String uid = intent.getStringExtra("uid");
                String userInfo = intent.getStringExtra("user_info");
                try {
                    String[] info = userInfo.split("-");
                    String name = info[0];
                    String group = info[1];
                    idTv.setText("编号:" + uid);
                    nameTv.setText("姓名:" + name);
                    groupTv.setText("项目组:" + group);

                    HashMap<String, String> params = new HashMap<>();
                    HashMap<String, String> headers = new HashMap<>();
                    params.put("id",uid);
                    params.put("name",name);
                    params.put("groupp",group);
                    headers.put(Constant.LOGIN_TOKEN,
                            sharedPreferences.getString(Constant.LOGIN_TOKEN, ""));
                    HttpUtil.getInstance().postAsyn("aiface/add/", params, headers,
                            new ReqCallBack<Msg>() {
                        @Override
                        public void onReqSuccess(Msg result) {
                            switch (result.getCode()) {
                                case Constant.CODE_SUCCESS:
                                    Toast.makeText(LoginResultActivity.this,
                                            "签到成功!", Toast.LENGTH_LONG).show();
                                    break;
                                case Constant.CODE_FAILED:
                                    Toast.makeText(LoginResultActivity.this,
                                        "服务器错误!", Toast.LENGTH_LONG).show();
                                        break;
                                case Constant.CODE_REPEAT:
                                    Toast.makeText(LoginResultActivity.this,
                                            "重复签到!", Toast.LENGTH_LONG).show();
                                    break;
                                case Constant.CODE_RE_LOGIN:
                                    startActivity(new Intent(
                                            LoginResultActivity.this, LoginActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                            Toast.makeText(LoginResultActivity.this,
                                    "服务器错误!", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(LoginResultActivity.this,
                            "服务器错误!", Toast.LENGTH_LONG).show();
                }

//                double score = intent.getDoubleExtra("score", 0);
//                if (TextUtils.isEmpty(userInfo)) {
//                    nameTv.setText(uid);
//                } else {
//                    nameTv.setText(userInfo);
//                }
//
//                groupTv.setText(String.valueOf(score));


            } else {
                idTv.setText("签到失败");
//                String uid = intent.getStringExtra("uid");
//                String errorMsg = intent.getStringExtra("error_msg");
//                nameTv.setText(uid);
//                groupTv.setText(String.valueOf(errorMsg));
            }
            headIv.setVisibility(View.VISIBLE);
            Bitmap bmp = ImageSaveUtil.loadCameraBitmap(this, "head_tmp.jpg");
            if (bmp != null) {
                headIv.setImageBitmap(bmp);
            }

        }

    }

    private void addListener() {
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (backBtn == v) {
            finish();
        }
    }

}
