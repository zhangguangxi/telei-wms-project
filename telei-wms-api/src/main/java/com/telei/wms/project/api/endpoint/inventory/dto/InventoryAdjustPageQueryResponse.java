package com.telei.wms.project.api.endpoint.inventory.dto;

        import com.nuochen.framework.autocoding.domain.Pagination;
        import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/10 18:30
 */
@Data
public class InventoryAdjustPageQueryResponse {
    /**分页信息*/
    private Pagination page;
}
