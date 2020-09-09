package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
* wms_adjt_line 库存调整单明细
*/
@Data
public class WmsAdjtLine implements Entity<Long> {
    /** id */
    private Long adjlId;
    /** 单头id */
    private Long adjhId;
    /** 库存id */
    private Long ivId;
    /** 库位编码 */
    private String lcCode;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 调整后库存id */
    private Long ivIdAdjt;
    /** 调整库位 */
    private String lcCodeAdjt;
    /** 调整类型，MOVE 移位，INCREASE 调增，REDUCE	调减，LIFTUP 升任务，LIFTDOWN 降任务 */
    private String ivAdjhType;
    /** 调整数量 */
    private BigDecimal ivQtyAdjt;
    @Override
    public Long getId() {
        return adjlId;
    }
    @Override
    public void setId(Long id) {
        this.adjlId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}