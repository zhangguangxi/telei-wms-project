package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsPloLine;
import lombok.Data;

/**
 * 拣货明细
 */
@Data
public class PloLinePageQueryResponseVo extends WmsPloLine {

    /** 拣货员 */
    private String plUser;
    /** 拣货时间 */
    private String plTime;
}
