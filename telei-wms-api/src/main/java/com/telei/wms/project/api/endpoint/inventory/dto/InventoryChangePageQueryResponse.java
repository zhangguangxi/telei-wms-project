package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/20 10:00
 */
@Data
public class InventoryChangePageQueryResponse {
    private Pagination pagination;
}
