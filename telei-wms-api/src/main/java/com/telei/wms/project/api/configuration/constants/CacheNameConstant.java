package com.telei.wms.project.api.configuration.constants;

/**
 * @author: leo
 * @date: 2020/7/16 14:21
 */
public abstract class CacheNameConstant {

    /**前缀*/
    private static final String PREFIX= "wms";
    /**分隔符*/
    private static final String DELIMITER="_";


    /**goods缓存名(演示样例)*/
    public static  final String GOODS_CACHE_NAME = PREFIX + DELIMITER + "goods" + DELIMITER;

    /**模拟单一业务(由于用同一个缓存演示，故家single进行区分)*/
    public static  final String SINGLE_WMS_GOODS_CACHE = PREFIX + DELIMITER + "single_goods" + DELIMITER;

}
