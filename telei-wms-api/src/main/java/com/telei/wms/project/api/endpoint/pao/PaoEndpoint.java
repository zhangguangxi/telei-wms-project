package com.telei.wms.project.api.endpoint.pao;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.pao.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 上架
 * @Auther: Dean
 * @Date: 2020/8/31 9:25
 */
@Api(tags = "上架单")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class PaoEndpoint {

    @Autowired
    private PaoBussiness paoBussiness;

    @ApiOperation("新增上架单")
    @PostMapping(ServiceId.WMS_PAO_ADD)
    public PaoCudBaseResponse addPao(@RequestBody @Valid PaoHeaderAddRequest request) {
        return paoBussiness.addPao(request);
    }

    @ApiOperation("分页查询上架单")
    @PostMapping(ServiceId.WMS_PAO_PAGE_QUERY)
    public PaoHeaderPageQueryResponse pageQueryRoHeader(@RequestBody @Valid PaoHeaderPageQueryRequest request) {
        return paoBussiness.pageQueryRoHeader(request);
    }

    @ApiOperation("查询上架单详细")
    @PostMapping(ServiceId.WMS_PAO_DETAIL)
    public PaoHeaderDetailResponse paoDetail(@RequestBody @Valid PaoDetailRequest request) {
        return paoBussiness.paoDetail(request);
    }

    @ApiOperation("修改上架单")
    @PostMapping(ServiceId.WMS_PAO_UPDATE)
    public PaoCudBaseResponse updateRoHeader(@RequestBody @Valid PaoHeaderUpdateRequest request) {
        return paoBussiness.updateRoHeader(request);
    }

    @ApiOperation("取消上架单")
    @PostMapping(ServiceId.WMS_PAO_CANCEL)
    public PaoCudBaseResponse cancelPaoHeader(@RequestBody @Valid PaoHeaderCancelRequest request) {
        return paoBussiness.cancelPaoHeader(request);
    }
}
