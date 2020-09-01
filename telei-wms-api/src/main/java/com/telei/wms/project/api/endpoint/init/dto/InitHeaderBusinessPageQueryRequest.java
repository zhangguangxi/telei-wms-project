package com.telei.wms.project.api.endpoint.init.dto;


import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class InitHeaderBusinessPageQueryRequest {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 单据编号
     */
    private String ivihCode;

    /**
     * 库存初始化状态
     */
    private String ivihStatus;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 所属人
     */
    private String createUser;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
