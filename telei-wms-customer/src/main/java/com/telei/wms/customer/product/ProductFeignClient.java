package com.telei.wms.customer.product;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author gongrp
 * @date 2020/7/3 14:35
 */
@FeignClient(value = "ProductFeignClient", url = "http://localhost:8811/")
public interface ProductFeignClient {

    /**
     * 根据条码获取产品详情
     * @param request
     * @return
     */
    @PostMapping("internal/010208")
    ApiResponse getProductDetailByBarCode(ProductDetailRequest request);

    /**
     * 根据ids查询
     * @param request
     * @return
     */
    @PostMapping("internal/010216")
    ApiResponse getProductList(ProductRequest request);

    /**
     * 根据条件查询产品列表
     * @param request
     * @return
     */
    @PostMapping("internal/010215")
    ApiResponse selectProductList(ProductDetailRequest request);

}
