package com.telei.wms.project.api.endpoint.liftWork.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_lift_work 升降任务
 */
@Data
public class LiftWorkUpdateCommonRequest {
    private Long liftId;

    @ApiModelProperty(value = "实际库位",example = "12345678",position = 7)
    @Check
    private String lcCode;

    @ApiModelProperty(value = "操作用户ID",example = "12345678",position = 7)
    @Check
    private Long operateUserId;

    @ApiModelProperty(value = "操作用户",example = "admin",position = 7)
    @Check
    private String operateUser;

}