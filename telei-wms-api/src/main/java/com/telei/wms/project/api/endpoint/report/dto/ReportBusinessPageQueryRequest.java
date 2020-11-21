package com.telei.wms.project.api.endpoint.report.dto;


import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class ReportBusinessPageQueryRequest {

    private String startTime;
    private String endTime;
    /** 当前时区数 东八区：8 */
    private Integer hour;
    /**当前页*/
    private Integer pageNumber;
    /**每页记录数*/
    private Integer pageSize;

}
