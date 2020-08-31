package com.telei.wms.commons.amqp;

import org.apache.tomcat.util.security.MD5Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Auther: Dean
 * @Date: 2020/7/16 17:30
 */
public class Signature {

    private static String secret = "NSKJF275JSFKS363HFFBHS2949FHNDFDJ";

    private static int timeout = 60 * 5;

    public static String make(String data, int timestamp) {
        try {
            String str = data + secret + timestamp;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return MD5Encoder.encode(md5.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("获取签名失败");
        }
    }

    public static void check(String data, int timestamp, String signature) {
        if (getTimestamp() - timestamp > timeout) {
            throw new RuntimeException("签名已过期");
        }
        if (! make(data, timestamp).equals(signature)) {
            throw new RuntimeException("签名失败");
        }
    }

    public static int getTimestamp() {
        Long time = System.currentTimeMillis() / 1000;
        return time.intValue();
    }
}
