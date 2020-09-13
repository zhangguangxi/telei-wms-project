package com.telei.wms.customer.product.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/13 21:00
 */
@Data
public class ProductRequest {
    private List<Long> productIds;
}
