package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 库位
 */
@Data
public class PaoLineLocationResponseVo {

    /** 产品id */
    private Long productId;
    /** 库位编码 */
    private String lcCode;
}
