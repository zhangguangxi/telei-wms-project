package com.telei.wms.project.api.endpoint.backlc.dto;

import com.telei.wms.commons.utils.CommonResponse;
import com.telei.wms.datasource.wms.vo.DoContainerDetailResponseVo;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 10:15
 */
@Data
public class BacklcOutStockContainerListBussinessResponse extends CommonResponse{
    List<DoContainerDetailResponseVo> list;
}
