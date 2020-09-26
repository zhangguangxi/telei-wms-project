package com.telei.wms.project.api.endpoint.liftWork.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * wms_lift_work 升降任务
 * @author gongrp
 */
@Data
public class LiftWorkInfoRequest {

    @ApiModelProperty(value = "产品id",example = "2",position = 7)
    @Check
    private Long productId;

    @ApiModelProperty(value = "仓库id",example = "2",position = 7)
    @Check
    private Long warehouseId;

    @ApiModelProperty(value = "升降任务类型",example = "RISE",position = 7)
    @Check
    private String liftType;

}