package com.telei.wms.project.api.endpoint.pao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaoLineAddRequest {

    /** 入库单单头id */
    @ApiModelProperty(value = "入库单id", example = "4675353938365515777")
    private Long roId;
    /** 对应计划明细的id */
    @ApiModelProperty(value = "入库单明细id", example = "4675353938365515777")
    private Long rolId;
    /** 收货作单头id */
    @ApiModelProperty(value = "收货单id", example = "4675353938365515777")
    private Long rooId;
    /** 收货单号 */
    @ApiModelProperty(value = "收货单号", example = "4675353938365515777")
    private String rooCode;
    /** 收货单收货明细id */
    @ApiModelProperty(value = "收货单明细id", example = "4675353938365515777")
    private Long roolId;
    /** 产品id */
    @ApiModelProperty(value = "产品id", example = "4675353938365515777")
    private Long productId;
    /** 单位毛重(KG) */
    @ApiModelProperty(value = "单位毛重", example = "1")
    private BigDecimal unitGrossWeight;
    /** 单价 */
    @ApiModelProperty(value = "单价", example = "1")
    private BigDecimal unitPrice;
    /** 单位体积(CBM) */
    @ApiModelProperty(value = "单位体积", example = "1")
    private BigDecimal unitVol;
    /** 备注 */
    @ApiModelProperty(value = "备注", example = "备注")
    private String memo;
    /** 上架数量 */
    @ApiModelProperty(value = "上架数量", example = "1")
    private BigDecimal paolQty;
    /** 计量单位 */
    @ApiModelProperty(value = "计量单位", example = "1")
    private Integer stockUnit;
    /** 明细行总重量(KG) */
    @ApiModelProperty(value = "明细行总重量", example = "1")
    private BigDecimal lineTotalWeight;
    /** 明细行净重(KG) */
    @ApiModelProperty(value = "明细行净重", example = "1")
    private BigDecimal lineNetWeight;
    /** 明细行总体积(CBM) */
    @ApiModelProperty(value = "明细行总体积", example = "1")
    private BigDecimal lineTotalVol;
    /** 库存批次id */
    @ApiModelProperty(value = "库存批次id", example = "1")
    private Long iabId;
}