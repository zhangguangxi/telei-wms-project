package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 库位
 */
@Data
public class PloLineLocationResponseVo {

    /** 产品id */
    private Long productId;
    /** 库位编码 */
    private String lcCode;
    /** 通道 */
    private String lcAisle;
    /** 货架 */
    private String lcX;
    private String lcY;
    private String lcZ;
}
