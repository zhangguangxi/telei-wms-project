package com.telei.wms.project.api.endpoint.pao.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaoLineAddRequest {

    /** 对应计划明细的id */
    @ApiModelProperty(value = "入库单明细id", example = "4675353938365515777")
    @Check
    private Long rolId;
    /** 收货单收货明细id */
    @ApiModelProperty(value = "收货单明细id", example = "4675353938365515777")
    @Check
    private Long roolId;
    /** 产品id */
    @ApiModelProperty(value = "产品id", example = "4675353938365515777")
    @Check
    private Long productId;
    /** 单位毛重(KG) */
    @ApiModelProperty(value = "单位毛重", example = "1")
    @Check
    private BigDecimal unitGrossWeight;
    /** 单位体积(CBM) */
    @ApiModelProperty(value = "单位体积", example = "1")
    @Check
    private BigDecimal unitVol;
    /** 备注 */
    @ApiModelProperty(value = "备注", example = "备注")
    private String memo;
    /** 上架数量 */
    @ApiModelProperty(value = "上架数量", example = "1")
    @Check
    private BigDecimal paolQty;
    /** 计量单位 */
    @ApiModelProperty(value = "计量单位", example = "1")
    @Check
    private Integer stockUnit;
    /** 明细行总重量(KG) */
    @ApiModelProperty(value = "明细行总重量", example = "1")
    @Check
    private BigDecimal lineTotalWeight;
    /** 明细行净重(KG) */
    @ApiModelProperty(value = "明细行净重", example = "1")
    @Check
    private BigDecimal lineNetWeight;
    /** 明细行总体积(CBM) */
    @ApiModelProperty(value = "明细行总体积", example = "1")
    @Check
    private BigDecimal lineTotalVol;
    /** 库存批次id */
    @ApiModelProperty(value = "库存批次id", example = "1")
    @Check
    private Long iabId;
}