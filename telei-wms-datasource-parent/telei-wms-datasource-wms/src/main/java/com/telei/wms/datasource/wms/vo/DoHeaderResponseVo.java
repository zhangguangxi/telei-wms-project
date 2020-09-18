package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsDoHeader;
import lombok.Data;

/**
 * 收货单分页查询
 */
@Data
public class DoHeaderResponseVo extends WmsDoHeader {

    /** 仓库名称 */
    private String warehouseName;
    /** 客户名称 */
    private String customerName;
    /** 供应商名称 */
    private String supplierName;

}
