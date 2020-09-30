package com.telei.wms.customer.supplier.dto;

import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class SupplierRequest {

    /**供应商名称*/
    private List<String> supplierNames;

    /**公司id*/
    private Long companyId;

}
