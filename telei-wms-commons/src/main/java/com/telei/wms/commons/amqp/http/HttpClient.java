package com.telei.wms.commons.amqp.http;

import com.alibaba.fastjson.JSON;
import com.telei.wms.commons.amqp.Signature;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @Auther: Dean
 * @Date: 2020/7/16 15:30
 */
public class HttpClient {

    public static ApiResponse post(String url, Object data) {
        String stringData = JSON.toJSONString(data);
        String signature = Signature.make(stringData, Signature.getTimestamp());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("signature", signature);
        headers.add("timestamp", String.valueOf(Signature.getTimestamp()));
        HttpEntity<String> requestEntity = new HttpEntity<String>(stringData, headers);
        RestTemplate http = new RestTemplate();
        ResponseEntity<ApiResponse> response= http.postForEntity(url, requestEntity, ApiResponse.class);
        //回调响应
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("回调接口调用失败");
        }
        return response.getBody();
    }
}
