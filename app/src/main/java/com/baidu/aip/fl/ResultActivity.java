package com.baidu.aip.fl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.aip.fl.adapter.ResultAdapter;
import com.baidu.aip.fl.bean.User;
import com.baidu.aip.fl.utils.Constant;
import com.baidu.aip.fl.utils.HttpUtil;
import com.baidu.aip.fl.utils.Msg;
import com.baidu.aip.fl.widget.LoadingDialog;
import com.hearing.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create by hearing on 18-10-17
 */
public class ResultActivity extends Activity {

    private TextView mResultTv;
    private ListView mResultLv;
    private SharedPreferences mSharedPreferences;
    private LoadingDialog mLoadingDialog;
    private List<User> mData;
    private ResultAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mResultLv = findViewById(R.id.result_lv);
        mResultTv = findViewById(R.id.result_tv);

        mSharedPreferences = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        mData = new ArrayList<>();

        getData();

        mAdapter = new ResultAdapter(this, mData);
        mResultLv.setAdapter(mAdapter);
    }

    private void getData() {
        showLoading();

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.LOGIN_TOKEN,
                mSharedPreferences.getString(Constant.LOGIN_TOKEN, ""));
        HttpUtil.getInstance().getAsyn("aiface/all/", null,
                headers, new ReqCallBack<Msg>() {
                    @Override
                    public void onReqSuccess(Msg result) {
                        hideLoading();

                        switch (result.getCode()) {
                            case Constant.CODE_SUCCESS:
                                try {
                                    JSONArray data = new JSONArray(result.getData().get(Constant.LIST_USERS).toString());
                                    for (int i = 0; i < data.length(); i++) {
                                        try {
                                            JSONObject jsonObject = data.getJSONObject(i);

                                            User user = new User(jsonObject.getString("id"),
                                                    jsonObject.getString("name"),
                                                    jsonObject.getString("groupp"));

                                            mData.add(user);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                mAdapter.notifyDataSetChanged();
                                mResultTv.setText("签到总人数:" + mData.size());
                                break;
                            case Constant.CODE_FAILED:
                                Toast.makeText(ResultActivity.this,
                                        "服务器错误!", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        hideLoading();
                        Toast.makeText(ResultActivity.this,
                                "服务器错误!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 显示加载的进度款
     */
    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, "加载中",
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

    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }
}
