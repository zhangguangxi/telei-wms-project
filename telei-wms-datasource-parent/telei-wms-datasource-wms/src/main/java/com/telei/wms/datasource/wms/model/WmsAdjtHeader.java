package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
* wms_adjt_header 库存调整单头
*/
@Data
public class WmsAdjtHeader implements Entity<Long> {
    /** id */
    private Long adjhId;
    /** 业务单据编号-按单据编码规则生成 */
    private String adjhCode;
    /** 公司编码 */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 调整类型，MOVE	移位，INCREASE 调增，REDUCE	调减，LIFTUP 升任务，LIFTDOWN 降任务 */
    private String adjhType;
    /**
     * 调增：调增、盘盈
     * 调减：调减、盘亏、销毁、报损
     * */
    private String adjhDetailType;
    /** 库存调整状态 01-制单，20-审核，98-关闭，不展示 */
    private String ivihStatus;
    /** 业务日期 */
    private Date bizDate;
    /** 原因 */
    private String reason;
    /** 产品序列码 */
    private Long productId;
    /** 库位编码 */
    private String lcCode;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 调整库位 */
    private String lcCodeAdjt;
    /** 调整数量 */
    private BigDecimal ivQtyAdjt;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 大包转换数 = 产品表大包转换数 */
    private Integer bigBagRate;
    /** 中包转换数 = 产品表中包转换数 */
    private Integer midBagRate;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
    /** 审核时间，同创建时间，不展示 */
    private Date auditTime;
    /** 审核用户，同创建用户，不展示 */
    private String auditUser;
    @Override
    public Long getId() {
        return adjhId;
    }
    @Override
    public void setId(Long id) {
        this.adjhId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
