package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.CommonResponse;
import lombok.Data;

import java.util.Map;

/**
 * @author: leo
 * @date: 2020/11/13 16:47
 */
@Data
public class InventoryMultiSampleLocationCheckResponse extends CommonResponse {
    private Map<String, String> lcCodeMap;
}
