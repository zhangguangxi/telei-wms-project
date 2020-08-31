package com.telei.wms.commons.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 编码解码器
 */
public class Codec {

    private Charset charset;

    private Codec(Charset charset) {
        this.charset = charset;
    }

    /**
     * 创建实例
     *
     * @param charset
     * @return
     */
    public static Codec create(Charset charset) {
        return new Codec(charset);
    }

    /**
     * 使用 UTF-8 编码创建实例
     *
     * @return
     */
    public static Codec createUseUtf8() {
        return new Codec(StandardCharsets.UTF_8);
    }

    /**
     * URL 编码
     *
     * @param text
     * @return
     */
    public String encodeUrl(String text) {
        try {
            return URLEncoder.encode(text, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL 解码
     *
     * @param text
     * @return
     */
    public String decodeUrl(String text) {
        try {
            return URLDecoder.decode(text, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public byte[] decodeBase64(byte[] data) {
        return Base64.decodeBase64(data);
    }

    /**
     * Base64 解码
     *
     * @param base64String
     * @return
     */
    public byte[] decodeBase64(String base64String) {
        return Base64.decodeBase64(base64String);
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public byte[] encodeBase64(String data) {
        return encodeBase64(data.getBytes(charset));
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public String encodeBase64String(String data) {
        return encodeBase64String(data.getBytes(charset));
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public byte[] encodeBase64URLSafe(String data) {
        return encodeBase64URLSafe(data.getBytes(charset));
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public String encodeBase64URLSafeString(String data) {
        return encodeBase64URLSafeString(data.getBytes(charset));
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public byte[] encodeBase64(byte[] data) {
        return Base64.encodeBase64(data);
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public String encodeBase64String(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public byte[] encodeBase64URLSafe(byte[] data) {
        return Base64.encodeBase64URLSafe(data);
    }

    /**
     * Base64 编码
     *
     * @param data
     * @return
     */
    public String encodeBase64URLSafeString(byte[] data) {
        return Base64.encodeBase64URLSafeString(data);
    }

}
