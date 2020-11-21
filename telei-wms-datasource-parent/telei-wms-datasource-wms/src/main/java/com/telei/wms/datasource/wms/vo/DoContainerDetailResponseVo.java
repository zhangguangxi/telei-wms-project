package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gongrp
 */
@Data
public class DoContainerDetailResponseVo {
    /**出库单头ID*/
    private Long dohId;
    /**柜id*/
    private Long cId;
    /**状态(已出库)*/
    private String orderStatus;
    /**出库时间*/
    private Date  shippingTime;
    /** 物流单 */
    private String shipmentCode;
    /** 装柜时间 */
    private Date loadTime;
    /** 装柜员 */
    private String loadUser;
    /** (数量)装箱数 */
    private BigDecimal cQty;
    /**总重量(装箱重量KG)*/
    private BigDecimal cWeight;
    /**总体积(装箱体积CBM)*/
    private BigDecimal cVol;

}
