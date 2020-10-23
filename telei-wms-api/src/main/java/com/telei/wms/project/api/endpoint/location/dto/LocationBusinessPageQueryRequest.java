package com.telei.wms.project.api.endpoint.location.dto;


import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class LocationBusinessPageQueryRequest {

    /**
     * 通道
     */
    private String lcAisle;

    /**
     * 库位编码
     */
    private String lcCode;

    /**
     * 库位类型，数据字典，S 样品库位、Z 高架库位 等
     */
    private String lcType;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /** 仓库id */
    private Long warehouseId;

    /**
     * 无锁:NO 有锁:YES 上架锁:PUTAWAY 下架锁:PICKING
     */
    private String lcLock;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
