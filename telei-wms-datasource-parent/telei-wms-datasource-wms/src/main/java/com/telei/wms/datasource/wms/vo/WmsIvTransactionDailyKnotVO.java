package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存快照日结统计数据
 * @author gongrp
 */
@Data
public class WmsIvTransactionDailyKnotVO extends WmsIvTransactionDailyKnot {
    /** 大包转换数 */
    private Integer bigBagQty;
    /** 单位毛重 */
    private BigDecimal unitGrossWeight;
    /** 单位体积 */
    private BigDecimal unitVol;
}
