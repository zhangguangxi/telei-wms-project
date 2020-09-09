package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_transaction 库存变动记录
 */
@Data
public class WmsIvTransaction implements Entity<Long> {
    /** id */
    private Long id;
    /** 应用类型代码，SHIP 签出扣减，PUTA 上架新增，ADJT 库存调整 */
    private String apCode;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 客户id */
    private Long customerId;
    /** 商品id */
    private Long productId;
    /** 先进先出时间 */
    private Date ivFifoTime;
    /** 库位编码 */
    private String lcCodeFrom;
    /** 原数量 */
    private BigDecimal ivQtyFrom;
    /** 变动后库位编码 */
    private String lcCodeTo;
    /** 调整后数量 */
    private BigDecimal ivQtyTo;
    /** 引起库存变动业务单据编号 */
    private String ivtDocumentCode;
    /** 引起库存变动单据id */
    private Long ivtDocumentId;
    /** 引起库存变动单据明细id */
    private Long ivtDocumentlineId;
    /** 库存变动类型，INCR 增，LESS 减，MOVE 移位 */
    private String ivtChangeType;
    /** 调整数量，数量不变此值为0 */
    private BigDecimal dcQty;
    /** 库存id */
    private Long ivId;
    /** 库存批次id */
    private Long iabId;
    /** 业务日期 */
    private Date bizDate;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
}