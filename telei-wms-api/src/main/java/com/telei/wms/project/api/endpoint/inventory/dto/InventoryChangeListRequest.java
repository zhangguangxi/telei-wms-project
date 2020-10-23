package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: leo
 * @date: 2020/10/20 15:19
 */
@Data
public class InventoryChangeListRequest {
    @ApiModelProperty(value = "createTime")
    @Check
    private Date createTime;
}
