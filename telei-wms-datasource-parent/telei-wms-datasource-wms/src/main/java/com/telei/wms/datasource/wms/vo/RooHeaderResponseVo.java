package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsRooHeader;
import lombok.Data;

/**
 * 收货单分页查询
 */
@Data
public class RooHeaderResponseVo extends WmsRooHeader {

    /** 仓库名称 */
    private String warehouseName;
    /** 客户名称 */
    private String customerName;
    /** 供应商名称 */
    private String supplierName;

}
