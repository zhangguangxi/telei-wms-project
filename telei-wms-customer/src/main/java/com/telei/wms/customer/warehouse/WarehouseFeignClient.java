package com.telei.wms.customer.warehouse;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.warehouse.dto.WarehouseDetailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author gongrp
 * @date 2020/7/3 14:35
 */
@FeignClient(value = "WarehouseFeignClient", url = "${customize.feign.cdm-url}")
public interface WarehouseFeignClient {

    /**
     * 查询公司默认仓库
     */
    @PostMapping("internal/012107")
    ApiResponse getCompanyWarehouse(WarehouseDetailRequest request);

}
