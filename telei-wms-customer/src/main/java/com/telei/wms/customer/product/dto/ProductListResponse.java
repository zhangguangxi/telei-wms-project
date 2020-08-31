package com.telei.wms.customer.product.dto;

import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class ProductListResponse {

    /**
     * 产品列表
     */
    private List<ProductDetailResponse> productList;

}
