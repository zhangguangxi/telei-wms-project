package com.telei.wms.project.api.endpoint.liftWork.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * 升降任务 Request
 */
@Data
public class LiftWorkRevokeRequest {

    @ApiModelProperty(value = "升降任务id列表", example = "4675353938365515777")
    @Check
    private Collection<Long> ids;

}