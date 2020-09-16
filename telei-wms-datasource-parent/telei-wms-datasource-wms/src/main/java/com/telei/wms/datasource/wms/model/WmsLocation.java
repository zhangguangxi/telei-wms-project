package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import lombok.Data;

/**
* wms_location 库位
*/
@Data
public class WmsLocation implements Entity<Long> {
    /** id */
    private Long locationId;
    /** 库位编码 */
    private String lcCode;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 通道 */
    private String lcAisle;
    /** 货架 */
    private String lcX;
    private String lcY;
    private String lcZ;
    /** 库位类型，数据字典，S 样品库位、E 高架库位 等 */
    private String lcType;
    /** 长(CM) */
    private BigDecimal lcLength;
    /** 宽(CM) */
    private BigDecimal lcWidth;
    /** 高(CM) */
    private BigDecimal lcHeight;
    /** 承重(kg) */
    private Integer lcSustainweight;
    /** 上架锁 */
    private String lcPutawaylock;
    /** 下架锁 */
    private String lcPickinglock;
    /** 备注 */
    private String memo;
    @Override
    public Long getId() {
        return locationId;
    }
    @Override
    public void setId(Long id) {
        this.locationId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
