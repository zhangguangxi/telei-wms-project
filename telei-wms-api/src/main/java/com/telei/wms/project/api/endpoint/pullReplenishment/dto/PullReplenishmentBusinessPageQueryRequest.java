package com.telei.wms.project.api.endpoint.pullReplenishment.dto;


import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class PullReplenishmentBusinessPageQueryRequest {

    private String startTime;

    private String endTime;

    /**
     * 产品编码
     */
    private String productNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 条码
     */
    private String productBarcode;

    /**
     * 供应商
     */
    private String supplierName;

    /**
     * 产品分类id
     */
    private Long productCategoryId;

    /**
     * 产品分类名称
     */
    private String productCategoryName;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
