package com.telei.wms.project.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Description: 临时测试工具类
 * @Auther: leo
 * @Date: 2020/6/9 11:01
 */
public class TempIdGeneratorUtil {
    /**
     * 生成id  时间戳+3位整数
     *
     * @return
     */
    public static long autoId() {

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        Date orderId = null;
        Random rand = new Random();
        //[900]：900个    100：从100
        int x = rand.nextInt(900) + 100;
        try {
            orderId = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.valueOf(String.valueOf(orderId.getTime()) + x);
    }

}
