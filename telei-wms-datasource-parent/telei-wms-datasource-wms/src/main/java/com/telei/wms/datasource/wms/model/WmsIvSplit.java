package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
* wms_iv_split 库存记录拆分表
*/
@Data
public class WmsIvSplit implements Entity<Long> {
    /** id */
    private Long ivspId;
    /** 原库存id */
    private Long ivId;
    /** 原库存数量 */
    private BigDecimal ivQty;
    /** 拆后库存数量 */
    private BigDecimal ivQtyAfter;
    /** 新库存id */
    private Long ivIdTo;
    /** 拆分库存数量 */
    private BigDecimal ivQtyTo;
    @Override
    public Long getId() {
        return ivspId;
    }
    @Override
    public void setId(Long id) {
        this.ivspId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
