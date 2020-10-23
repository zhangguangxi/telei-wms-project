package com.telei.wms.project.api.endpoint.nestPrint.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/12 09:18
 */
@Data
public class NestPaoPrintDetailRequest {
    @ApiModelProperty(value = "主键id", example = "4675353938365515777", position = 1)
    @Check
    private Long id;
}
