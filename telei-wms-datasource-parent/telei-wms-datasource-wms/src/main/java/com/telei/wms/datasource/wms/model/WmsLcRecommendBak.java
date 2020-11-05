package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * wms_lc_recommend_bak 新品分配推荐库位
 */
@Data
public class WmsLcRecommendBak implements Entity<Long> {
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
    /** 备份时间 */
    private Date delTime;
    /** 删除此条记录的来源数据的库位编码 */
    private String delLcCode;
    /** 删除此条记录的来源数据的单据code/或者拼接信息 */
    private String delRemark;
}