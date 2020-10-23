package com.telei.wms.project.api.endpoint.inventory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.telei.wms.commons.utils.PageCommonRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/20 10:00
 */
@Data
public class InventoryChangePageQueryRequest {
    private PageCommonRequest pageCommonRequest;
    private InventoryChangePageQueryCondition inventoryChangePageQueryCondition;

    @Data
    public static class InventoryChangePageQueryCondition {
        @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String startTime;

        @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String endTime;
    }
}
