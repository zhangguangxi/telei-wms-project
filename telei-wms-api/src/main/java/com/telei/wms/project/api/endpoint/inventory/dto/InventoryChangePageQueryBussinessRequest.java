package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.PageCommonRequest;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/20 10:00
 */
@Data
public class InventoryChangePageQueryBussinessRequest {
    private PageCommonRequest pageCommonRequest;
    private InventoryChangePageQueryCondition inventoryChangePageQueryCondition;

    @Data
    public static class InventoryChangePageQueryCondition {
        /**开始时间*/
        private String startTime;
        /**结束时间*/
        private String endTime;
    }
}
