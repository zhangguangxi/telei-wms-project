package com.telei.wms.project.api.endpoint.roo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * 撤销收货 Request
 */
@Data
public class RooHeaderRevokeRequest {

    @ApiModelProperty(value = "收货单id", example = "4675353938365515777")
    @Check
    private Long rooId;

    @ApiModelProperty(value = "收货单明细id列表", example = "[4675353938365515777,4675353938365515777]")
    @Check
    private Collection<Long> roolIds;

}