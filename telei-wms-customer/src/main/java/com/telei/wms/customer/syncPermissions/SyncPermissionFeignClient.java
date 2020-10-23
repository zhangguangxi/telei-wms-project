package com.telei.wms.customer.syncPermissions;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.syncPermissions.dto.SyncPermissionFeignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/18 10:00
 */
@FeignClient(value = "CdmFeignClient", url = "${customize.feign.cdm-url}")
public interface SyncPermissionFeignClient {
    /**
     * 令牌校验
     *
     * @return
     */
    @PostMapping("internal/012801")
    ApiResponse syncPermissions(SyncPermissionFeignRequest request);
}
