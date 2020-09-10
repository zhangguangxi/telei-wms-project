package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:19
 */
@Data
public class InventoryPageQueryBussinessResponse {
    /**分页信息*/
    private Pagination page;
}
