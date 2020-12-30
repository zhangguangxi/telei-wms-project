package com.telei.wms.project.api.endpoint.plo;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.plo.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 拣货单
 * @Auther: Dean
 * @Date: 2020/9/15 10:20
 */
@Api(tags = "拣货单")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class PloEndpoint {

    @Autowired
    private PloBussiness ploBussiness;

    @ApiOperation("新增拣货单")
    @PostMapping(ServiceId.WMS_PLO_ADD)
    public PloAddResponse addPlo(@RequestBody @Valid PloHeaderAddRequest request) {
        return ploBussiness.addPlo(request);
    }

    @ApiOperation("分页查询拣货单")
    @PostMapping(ServiceId.WMS_PLO_HEADER_PAGE_QUERY)
    public PloHeaderPageQueryResponse pageQueryPloHeader(@RequestBody @Valid PloHeaderPageQueryRequest request) {
        return DataConvertUtil.parseDataAsObject(ploBussiness.pageQueryPloHeader(request), PloHeaderPageQueryResponse.class);
    }

    @ApiOperation("查询拣货单详细")
    @PostMapping(ServiceId.WMS_PLO_HEADER_DETAIL)
    public PloHeaderResponse ploHeaderDetail(@RequestBody @Valid PloLineRequest request) {
        return DataConvertUtil.parseDataAsObject(ploBussiness.ploHeaderDetail(request), PloHeaderResponse.class);
    }

    @ApiOperation("新增拣货单详情")
    @PostMapping(ServiceId.WMS_PLO_ADD_DETAIL)
    public PloCudBaseResponse addPloDetail(@RequestBody @Valid List<PloDetailAddRequest> request) {
        return ploBussiness.addPloDetail(request);
    }

    @ApiOperation("分页查询拣货单详情")
    @PostMapping(ServiceId.WMS_PLO_DETAIL_PAGE_QUERY)
    public List<PloDetailPageQueryResponse> pageQueryPloDetail(@RequestBody @Valid PloDetailPageQueryRequest request) {
        return DataConvertUtil.parseDataAsArray(ploBussiness.pageQueryPloDetail(request), PloDetailPageQueryResponse.class);
    }

    @ApiOperation("拣货完成")
    @PostMapping(ServiceId.WMS_PLO_FINISH)
    public PloCudBaseResponse ploFinish(@RequestBody @Valid PloFinishRequest request) {
        return ploBussiness.ploFinish(request);
    }

    @ApiOperation("取消拣货单")
    @PostMapping(ServiceId.WMS_PLO_CANCEL_HEADER)
    public PloCudBaseResponse cancelPloHeader(@RequestBody @Valid PloHeaderCancelRequest request) {
        return ploBussiness.cancelPloHeader(request);
    }

    @ApiOperation("取消拣货记录")
    @PostMapping(ServiceId.WMS_PLO_CANCEL_DETAIL)
    public PloCudBaseResponse cancelPloDetail(@RequestBody @Valid PloDetailCancelRequest request) {
        return ploBussiness.cancelPloDetail(request);
    }

    @ApiOperation("取消拣货完成")
    @PostMapping(ServiceId.WMS_PLO_CANCEL_FINISH)
    public PloCudBaseResponse cancelPloFinish(@RequestBody @Valid PloHeaderCancelRequest request) {
        return ploBussiness.cancelPloFinish(request);
    }
}
