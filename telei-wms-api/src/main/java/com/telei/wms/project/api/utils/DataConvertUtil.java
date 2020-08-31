package com.telei.wms.project.api.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @Description: 数据转换
 * @Auther: leo
 * @Date: 2020/6/9 10:45
 */
public  class DataConvertUtil {

    /**
     * 解析数据作为一个对象
     * @param data
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseDataAsObject(Object data,Class<T> type){
        return JSON.parseObject(JSON.toJSONString(data),type);
    }

    /**
     * 解析
     * @param <T>
     * @return
     */
    public static  <T> List<T> parseDataAsArray(Object data,Class<T> type){
        return JSON.parseArray(JSON.toJSONString(data),type);
    }

}
