package com.telei.wms.project.api.endpoint.roo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * wms_roo_header 收货单
 */
@Data
public class RooHeaderUpdateRequest {
    /** id */
    private Long id;
    /** 生成上架单数量 */
    private BigDecimal tmpPutawayQty;
    /** 上架数量 */
    private BigDecimal putawayQty;
}