package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.CommonResponse;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/21 16:14
 */
@Data
public class InventoryDeductBussinessResponse extends CommonResponse {
    List<WmsIvTransactionDailyKnot> list;
}
