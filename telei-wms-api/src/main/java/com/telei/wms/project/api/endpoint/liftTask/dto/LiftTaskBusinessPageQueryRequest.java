package com.telei.wms.project.api.endpoint.liftTask.dto;


import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class LiftTaskBusinessPageQueryRequest {

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
     * 样品库位(升,为原库位; 降，为目标库位)
     */
    private String lcCode;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
