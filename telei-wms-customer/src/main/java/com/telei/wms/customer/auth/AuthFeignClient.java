package com.telei.wms.customer.auth;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.auth.dto.AuthVerifyFeignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/18 10:00
 */
@FeignClient(value = "AuthFeignClient",url="http://192.168.1.114:20010/")
public interface AuthFeignClient {
    /**
     * 令牌校验
     * @return
     */
    @PostMapping("internal/990102")
    ApiResponse authVerify(AuthVerifyFeignRequest request);
}
