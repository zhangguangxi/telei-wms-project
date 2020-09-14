package com.telei.wms.project.api.endpoint.ro;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.ro.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Auther: Dean
 * @Date: 2020/8/25 10:10
 */
@Api(tags = "入库任务")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class RoEndpoint {

    @Autowired
    private RoBussiness roBussiness;

    @ApiOperation("分页查询入库任务")
    @PostMapping(ServiceId.WMS_RO_HEADER_PAGE_QUERY)
    public RoHeaderPageQueryResponse pageQueryRoHeader(@RequestBody @Valid RoHeaderPageQueryRequest request) {
        return roBussiness.pageQueryRoHeader(request);
    }

    @ApiOperation("查询入库任务详细")
    @PostMapping(ServiceId.WMS_RO_DETAIL)
    public RoHeaderDetailResponse roDetail(@RequestBody @Valid RoDetailRequest request) {
        return roBussiness.roDetail(request);
    }

    @ApiOperation("修改入库任务主单")
    @PostMapping(ServiceId.WMS_RO_HEADER_UPDATE)
    public RoCudBaseResponse updateRoHeader(@RequestBody @Valid RoHeaderUpdateRequest request) {
        return roBussiness.updateRoHeader(request);
    }
}
