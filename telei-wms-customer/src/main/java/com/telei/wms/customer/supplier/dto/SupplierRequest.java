package com.telei.wms.customer.supplier.dto;

import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class SupplierRequest {

    /**供应商名称*/
    private String supplierName;

    /**公司id*/
    private Long companyId;

}
