package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsDoContainer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gongrp
 */
@Data
public class DoContainerResponseVo extends WmsDoContainer {

    /** 装柜时间 */
    private Date loadTime;

    /** 装柜员 */
    private String loadUser;

    /** 物流单 */
    private String shipmentCode;

    /** 大包数量 */
    private BigDecimal sumBigBagQty;

    /** 中包数量 */
    private BigDecimal sumMidBagQty;

    /** 小包数量 */
    private BigDecimal sumSmallBagQty;

    /** 装箱数量 */
    private BigDecimal sumQty;

}
