package com.telei.wms.project.api.endpoint.roo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class RooHeaderBusinessPageQueryRequest {

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
    private String rooCode;

    /**
     * 入库单据编号
     */
    private String roCode;

    /**
     * 收货状态
     */
    private String roStatus;

    /**
     * 所属人
     */
    private String ownerUser;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
