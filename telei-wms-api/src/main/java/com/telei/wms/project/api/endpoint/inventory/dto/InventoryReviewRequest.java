package com.telei.wms.project.api.endpoint.inventory.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:18
 */
@Data
public class InventoryReviewRequest {
    @ApiModelProperty(value = "库存调整单单头id",example = "123412312",position = 1)
    private Long adjhId;
}
