package com.telei.wms.customer.auth.dto;

import lombok.Data;

/**
 * @Description: token校验请求体
 * @Auther: leo
 * @Date: 2020/6/18 10:27
 */
@Data
public class AuthVerifyFeignRequest {
    /**令牌*/
    private String token;
    /**接口编码*/
    private String serviceId;
}
