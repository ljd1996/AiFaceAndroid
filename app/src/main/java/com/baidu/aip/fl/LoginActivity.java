package com.baidu.aip.fl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.aip.fl.utils.Constant;
import com.baidu.aip.fl.utils.HttpUtil;
import com.baidu.aip.fl.utils.Msg;
import com.baidu.aip.fl.widget.LoadingDialog;
import com.hearing.R;

import java.util.HashMap;

/**
 * Create by hearing on 18-10-17
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    //布局内的控件
    private EditText et_name;
    private EditText et_password;
    private EditText et_server;
    private Button mLoginBtn;
    private ImageView iv_see_password;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);

        initViews();
        setupEvents();

        checkFirst();
    }

    private void checkFirst() {
        HttpUtil.sBaseUrl = mSharedPreferences.getString(Constant.SERVER_URL, "");
        if (HttpUtil.sBaseUrl.isEmpty()) {
            return;
        }
        String token = mSharedPreferences.getString(Constant.LOGIN_TOKEN, "");
        if (token.isEmpty()) {
            return;
        }

        showLoading();//显示加载框
        Log.d("LLL", "token = " + token);

        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.LOGIN_TOKEN, token);
        HttpUtil.getInstance().postAsyn("aiface/validate/", map, null, new ReqCallBack<Msg>() {
            @Override
            public void onReqSuccess(Msg result) {
                hideLoading();//隐藏加载框

                if (result.getCode() == Constant.CODE_SUCCESS) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    showToast("登录信息已失效,请重新登录!");
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.d("LLL", errorMsg);
                hideLoading();//隐藏加载框
                showToast("登录信息已失效,请重新登录!");
            }
        });
    }

    private void initViews() {
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        et_name = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_server = (EditText) findViewById(R.id.et_server);
        iv_see_password = (ImageView) findViewById(R.id.iv_see_password);
    }

    private void setupEvents() {
        mLoginBtn.setOnClickListener(this);
        iv_see_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login(); //登陆
                break;
            case R.id.iv_see_password:
                setPasswordVisibility();
                break;
        }
    }

    /**
     * 模拟登录情况
     * 用户名csdn，密码123456，就能登录成功，否则登录失败
     */
    private void login() {

        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (getAccount().isEmpty()) {
            showToast("你输入的账号为空！");
            return;
        }

        if (getPassword().isEmpty()) {
            showToast("你输入的密码为空！");
            return;
        }

        if (getServer().isEmpty()) {
            showToast("你输入的服务器为空！");
            return;
        }

        HttpUtil.sBaseUrl = getServer();

        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框

        HashMap<String, String> map = new HashMap<>();
        map.put("name", getAccount());
        map.put("password", getPassword());
        HttpUtil.getInstance().postAsyn("aiface/login/", map, null, new ReqCallBack<Msg>() {
            @Override
            public void onReqSuccess(Msg result) {
                hideLoading();//隐藏加载框

                if (result.getCode() == Constant.CODE_SUCCESS) {
                    mEditor = mSharedPreferences.edit();
                    mEditor.putString(Constant.LOGIN_TOKEN,
                            (String) result.getData().get(Constant.LOGIN_TOKEN));
                    mEditor.putString(Constant.SERVER_URL, HttpUtil.sBaseUrl);
                    mEditor.apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    showToast("登录失败,请检查输入信息!");
                    setLoginBtnClickable(true);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.d("LLL", errorMsg);
                hideLoading();//隐藏加载框
                showToast("登录失败,请检查输入信息!");
                setLoginBtnClickable(true);  //这里解放登录按钮，设置为可以点击
            }
        });
    }


    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            //密码不可见
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            //密码可见
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    /**
     * 获取账号
     */
    private String getAccount() {
        return et_name.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    private String getPassword() {
        return et_password.getText().toString().trim();//去掉空格
    }

    /**
     * 获取服务器
     */
    private String getServer() {
        return et_server.getText().toString().trim();//去掉空格
    }


    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    private void setLoginBtnClickable(boolean clickable) {
        mLoginBtn.setClickable(clickable);
    }


    /**
     * 显示加载的进度款
     */
    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.loading),
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

    /**
     * 监听回退键
     */
    @Override
    public void onBackPressed() {
        Log.d("LLL", "onBackPressed...");
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    /**
     * 页面销毁前回调的方法
     */
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }


    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
