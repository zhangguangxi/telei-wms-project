package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_lift_work 升降任务表
 */
@Data
public class WmsLiftWork implements Entity<Long> {
    /** id */
    private Long id;
    /** 公司编码 */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 业务单据编号-按单据编码规则生成 */
    private String liftCode;
    /** 升降任务状态 10-待处理，20-已处理，98-关闭 */
    private String liftStatus;
    /** 升降类型 RISE-升库,DROP-降货 */
    private String liftType;
    /** 产品id */
    private Long productId;
    /** 产品编码 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 数量 */
    private BigDecimal liftQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包转换数 */
    private BigDecimal bigBagRate;
    /** 大包剩余数量 */
    private BigDecimal bigBagExtraQty;
    /** 样品库位(升,为原库位; 降，为目标库位) */
    private String sampleLcCode;
    /** 推荐库位 */
    private String prepLcCode;
    /** 实际库位 */
    private String lcCode;
    /** 来源单据的类型 IV 安全库存，ADD	手动创建， RECV 入库单收货， DOMT 出库订单， ADJT 库存调整单 */
    private String liftDocumentType;
    /** 引起升降业务的单据编号 */
    private String liftDocumentCode;
    /** 引起升降业务的单据id */
    private Long liftDocumentId;
    /** 引起升降业务的单据明细id */
    private Long liftDocumentlineId;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
    /** 操作时间 */
    private Date operateTime;
    /** 操作用户ID */
    private Long operateUserId;
    /** 操作用户 */
    private String operateUser;
    /** 修改时间 */
    private Date lastupdateTime;
    /** 修改用户 */
    private String lastupdateUser;
    /** 备注 */
    private String memo;
}