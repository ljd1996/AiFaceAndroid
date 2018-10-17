package com.baidu.aip.fl.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by hearing on 18-10-17
 */
public class Msg {
    private int code;
    private String msg;
    private Map<String, Object> data = new HashMap<>();

    public static Msg success(){
        Msg result = new Msg();
        result.setCode(Constant.CODE_SUCCESS);
        result.setMsg("处理成功!");
        return result;
    }

    public static Msg fail(){
        Msg result = new Msg();
        result.setCode(Constant.CODE_FAILED);
        result.setMsg("处理失败!");
        return result;
    }

    public Msg add(String key,Object value){
        this.getData().put(key, value);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
