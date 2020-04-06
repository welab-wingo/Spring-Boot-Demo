package com.wingo.mybatisplus.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * @Author Wingo
 * @Date 2020-04-06 18:47
 * @Description
 */

public class ResponseVO extends HashMap<String, Object> {

    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer ERROR_STATUS = -1;
    private static final String SUCCESS_MSG = "一切正常";
    private static final String ERROR_MSG = "出现错误";

    private static final long serialVersionUID = 1L;

    public ResponseVO success(String msg) {
        put("msg", SUCCESS_MSG);
        put("status", SUCCESS_STATUS);
        return this;
    }

    public ResponseVO error(String msg) {
        put("msg", ERROR_MSG);
        put("status", ERROR_STATUS);
        return this;
    }

    public ResponseVO setData(String key, Object obj) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> data = (HashMap<String, Object>) get("data");
        if (data == null) {
            data = new HashMap<>(16);
            put("data", data);
        }
        data.put(key, obj);
        return this;
    }

    /**
     * 返回JSON字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
