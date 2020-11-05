package com.telei.wms.project.api.endpoint.lcRecommend;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendCudBaseResponse;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendPageQueryRequest;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendPageQueryResponse;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendUpdateRequest;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 新品分配推荐库位
 * @Auther: Dean
 * @Date: 2020/11/3 17:50
 */
@Api(tags = "新品分配推荐库位")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class LcRecommendEndpoint {

    @Autowired
    private LcRecommendBussiness lcRecommendBussiness;

    @ApiOperation("更新推荐库位")
    @PostMapping(ServiceId.WMS_LC_RECOMMEND_UPDATE)
    public LcRecommendCudBaseResponse updateLcRecommend(@RequestBody @Valid LcRecommendUpdateRequest request) {
        return lcRecommendBussiness.updateLcRecommend(request);
    }

    @ApiOperation("分页查询推荐库位")
    @PostMapping(ServiceId.WMS_LC_RECOMMEND_PAGE_QUERY)
    public LcRecommendPageQueryResponse pageQueryLcRecommend(@RequestBody @Valid LcRecommendPageQueryRequest request) {
        return DataConvertUtil.parseDataAsObject(lcRecommendBussiness.pageQueryLcRecommend(request), LcRecommendPageQueryResponse.class);
    }
}
