package com.telei.wms.project.api.endpoint.location.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationBusinessResponse extends LocationCommonResponse {
    /** id */
    private Long locationId;
    /** 库位编码 */
    private String lcCode;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 通道 */
    private String lcAisle;
    /** 货架 */
    private String lcX;
    private String lcY;
    private String lcZ;
    /** 库位类型，数据字典，S 样品库位、Z 高架库位 等 */
    private String lcType;
    /** 长(CM) */
    private BigDecimal lcLength;
    /** 宽(CM) */
    private BigDecimal lcWidth;
    /** 高(CM) */
    private BigDecimal lcHeight;
    /** 体积CBM */
    private BigDecimal lcVol;
    /** 承重(kg) */
    private Integer lcSustainweight;
    /** 上架锁 */
    private String lcPutawaylock;
    /** 下架锁 */
    private String lcPickinglock;
    /** 备注 */
    private String memo;
}