package com.telei.wms.customer.businessNumber;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberListRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Auther: Dean
 * @Date: 2020/7/1 15:10
 */
@FeignClient(value = "BusinessNumberFeignClient", url = "http://localhost:8811/")
public interface BusinessNumberFeignClient {

    /**
     * 获取单个业务单号
     * @param request
     * @return
     */
    @PostMapping("internal/012001")
    ApiResponse get(BusinessNumberRequest request);

    /**
     * 批量获取业务单号
     * @param request
     * @return
     */
    @PostMapping("internal/012002")
    ApiResponse getList(BusinessNumberListRequest request);
}
