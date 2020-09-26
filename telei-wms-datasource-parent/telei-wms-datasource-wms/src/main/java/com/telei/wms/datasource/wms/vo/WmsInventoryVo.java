package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/22 10:56
 */
@Data
public class WmsInventoryVo {

    private String lcCode;

    private BigDecimal qty;

}
