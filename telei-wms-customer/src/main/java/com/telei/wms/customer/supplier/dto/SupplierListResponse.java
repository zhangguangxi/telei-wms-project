package com.telei.wms.customer.supplier.dto;

import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class SupplierListResponse {

    /**
     * 供应商列表
     */
    private List<SupplierResponse> supplierList;

}
