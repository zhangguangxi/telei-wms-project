package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:18
 */
@Data
public class InventoryPageQueryResponse  {
    /**分页信息*/
    private Pagination page;
}
