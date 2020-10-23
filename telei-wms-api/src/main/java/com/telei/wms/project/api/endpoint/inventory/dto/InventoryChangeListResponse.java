package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/20 15:19
 */
@Data
public class InventoryChangeListResponse {
    List<WmsIvTransactionDailyKnot> list;
}
