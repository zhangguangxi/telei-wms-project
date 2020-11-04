package com.telei.wms.customer.product.dto;

import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class ProductCategoryRequest {

    private List<Long> productCategoryIds;

}
