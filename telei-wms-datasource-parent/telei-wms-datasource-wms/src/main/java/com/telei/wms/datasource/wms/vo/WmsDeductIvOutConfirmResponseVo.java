package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/22 10:56
 */
@Data
public class WmsDeductIvOutConfirmResponseVo {
    /**库存锁定扣减id*/
    private Long ivId;
    /**扣减次数*/
    private Integer ivIdIndex;
}
