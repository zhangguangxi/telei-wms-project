package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增拣货详情Request
 */
@Data
public class PloDetailAddRequest {

    /** 单头id */
    @ApiModelProperty(value = "单头id", example = "4675353938365515777")
    @Check
    private Long ploId;
    /** 明细id */
    @ApiModelProperty(value = "明细id", example = "4675353938365515777")
    @Check
    private Long plolId;
    /** 拣货数量 */
    @ApiModelProperty(value = "拣货数量", example = "1")
    @Check
    private BigDecimal pickQty;
    /** 总重量(KG) */
    @ApiModelProperty(value = "总重量(KG)", example = "1")
    @Check
    private BigDecimal pickWeight;
    /** 总体积(CBM) */
    @ApiModelProperty(value = "总体积(CBM)", example = "1")
    @Check
    private BigDecimal pickVol;
    /** 创建用户 */
    @ApiModelProperty(value = "拣货员", example = "1")
    @Check
    private String createUser;
}