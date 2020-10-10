package com.telei.wms.customer.supplier;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.supplier.dto.SupplierDetailRequest;
import com.telei.wms.customer.supplier.dto.SupplierRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author gongrp
 * @date 2020/7/3 14:35
 */
@FeignClient(value = "SupplierFeignClient", url = "http://localhost:8811/")
public interface SupplierFeignClient {

    /**
     * 根据条码获取产品详情
     * @param request
     * @return
     */
    @PostMapping("internal/011712")
    ApiResponse getSupplierListByName(SupplierRequest request);


    /**
     * 根据供应商id查询供应商详情
     * @param request
     * @return
     */
    @PostMapping("internal/011707")
    ApiResponse getSupplierDetailById(SupplierDetailRequest request);
}
