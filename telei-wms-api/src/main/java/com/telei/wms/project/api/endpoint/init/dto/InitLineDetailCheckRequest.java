package com.telei.wms.project.api.endpoint.init.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品检查Request
 */
@Data
public class InitLineDetailCheckRequest {

    @ApiModelProperty(value = "条码", example = "46755515777")
    @Check
    private String productBarcode;

    @ApiModelProperty(value = "供应商名称", example = "供应商名称")
    @Check
    private String supplierName;

    @ApiModelProperty(value = "库位编码", example = "S5-01-15")
    @Check
    private String lcCode;

}