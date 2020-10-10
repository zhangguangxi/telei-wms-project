package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsRooLine;
import lombok.Data;

/**
 * 收货单分页查询
 */
@Data
public class RooLineResponseVo extends WmsRooLine {

    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 品牌 */
    private String brand;
    /** 产地 */
    private String placeOfOrigin;

}
