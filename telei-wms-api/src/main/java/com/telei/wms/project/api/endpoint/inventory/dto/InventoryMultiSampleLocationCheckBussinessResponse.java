package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.CommonResponse;
import lombok.Data;

import java.util.Map;

/**
 * @author: leo
 * @date: 2020/11/13 16:48
 */
@Data
public class InventoryMultiSampleLocationCheckBussinessResponse extends CommonResponse {
    private Map<String, String> lcCodeMap;
}
