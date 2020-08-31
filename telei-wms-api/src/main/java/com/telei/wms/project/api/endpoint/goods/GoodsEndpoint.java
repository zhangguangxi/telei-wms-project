package com.telei.wms.project.api.endpoint.goods;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.goods.dto.*;
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
 * @Description: example
 * @Auther: leo
 * @Date: 2020/6/9 09:35
 */
@Api(tags = "商品接口(演示样例v1.0)")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class GoodsEndpoint {

    @Autowired
    private GoodsBussiness goodsBussiness;


    @ApiOperation("新增商品")
    @PostMapping(ServiceId.WMS_GOODS_ADD)
    public GoodsAddResponse addGoods(@RequestBody @Valid GoodsAddRequest request){
        GoodsBusinessAddRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessAddRequest.class);
        GoodsBusinessAddResponse businessResponse = goodsBussiness.addGoods(businessRequest);
        GoodsAddResponse response = DataConvertUtil.parseDataAsObject(businessResponse, GoodsAddResponse.class);
        return response;
    }


    @ApiOperation("修改商品")
    @PostMapping(ServiceId.WMS_GOODS_UPDATE)
    public GoodsUpdateResponse updateGoods(@RequestBody @Valid GoodsUpdateRequest request){
        GoodsBusinessUpdateRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessUpdateRequest.class);
        GoodsBusinessUpdateResponse businessResponse = goodsBussiness.updateGoods(businessRequest);
        GoodsUpdateResponse response = DataConvertUtil.parseDataAsObject(businessResponse, GoodsUpdateResponse.class);
        return response;
    }

    @ApiOperation("删除商品")
    @PostMapping(ServiceId.WMS_GOODS_DELETE)
    public GoodsDeleteResponse deleteGoods(@RequestBody @Valid GoodsDeleteRequest request){
        GoodsBusinessDeleteRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessDeleteRequest.class);
        GoodsBusinessDeleteResponse businessResponse = goodsBussiness.deleteGoods(businessRequest);
        GoodsDeleteResponse response = DataConvertUtil.parseDataAsObject(businessResponse, GoodsDeleteResponse.class);
        return response;
    }

    @ApiOperation("详情商品")
    @PostMapping(ServiceId.WMS_GOODS_DETAIL)
    public GoodsDetailResponse detailGoods(@RequestBody @Valid GoodsDetailRequest request){
        GoodsBusinessDetailRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessDetailRequest.class);
        GoodsBusinessDetailResponse businessResponse = goodsBussiness.detailGoods(businessRequest);
        return  DataConvertUtil.parseDataAsObject(businessResponse, GoodsDetailResponse.class);
    }

    @ApiOperation("商品列表")
    @PostMapping(ServiceId.WMS_GOODS_LIST)
    public GoodsListResponse listGoods(@RequestBody @Valid GoodsListRequest request){
        GoodsBusinessListRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessListRequest.class);
        GoodsBusinessListResponse businessResponse = goodsBussiness.listGoods(businessRequest);
        return  DataConvertUtil.parseDataAsObject(businessResponse, GoodsListResponse.class);

    }

    @ApiOperation("商品分页")
    @PostMapping(ServiceId.WMS_GOODS_PAGE_QUERY)
    public GoodsPageQueryResponse pageGoodsQuery(@RequestBody GoodsPageQueryRequest request){
        GoodsBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, GoodsBusinessPageQueryRequest.class);
        GoodsBusinessPageQueryResponse businessResponse = goodsBussiness.pageQueryGoods(businessRequest);
        GoodsPageQueryResponse response = DataConvertUtil.parseDataAsObject(businessResponse, GoodsPageQueryResponse.class);
        return response;
    }
}
