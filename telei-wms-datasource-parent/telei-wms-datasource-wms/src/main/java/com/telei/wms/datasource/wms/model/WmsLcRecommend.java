package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * wms_lc_recommend 新品分配推荐库位
 */
@Data
public class WmsLcRecommend implements Entity<Long> {
    /** id */
    private Long id;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 预计入库时间（为空即无限大） */
    private Date estArriveTime;
    /** 产品id */
    private Long productId;
    /** 库位编码 */
    private String lcCode;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private String createUser;
    /** 最后更新时间 */
    private Date lastUpdateTime;
    /** 最后更新人 */
    private String lastUpdateUser;
}