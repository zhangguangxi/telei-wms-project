package com.telei.wms.project.api.endpoint.pao.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaoHeaderAddRequest {

    /** 收货作单头id */
    @ApiModelProperty(value = "收货单id", example = "1")
    @Check
    private Long rooId;
    /** 商家订单号 */
    @ApiModelProperty(value = "商家订单号", example = "4675353938365515777")
    @Check
    private String custOrderNo;
    /** 总数量 */
    @ApiModelProperty(value = "总数量", example = "1")
    @Check
    private BigDecimal totalQty;
    /** 总重量(KG) */
    @ApiModelProperty(value = "总重量", example = "1")
    @Check
    private BigDecimal totalWeight;
    /** 总净重(KG) */
    @ApiModelProperty(value = "总净重", example = "1")
    @Check
    private BigDecimal totalNetWeight;
    /** 总体积(CBM) */
    @ApiModelProperty(value = "总体积", example = "1")
    @Check
    private BigDecimal totalVol;

    private List<PaoLineAddRequest> paoLines;
}