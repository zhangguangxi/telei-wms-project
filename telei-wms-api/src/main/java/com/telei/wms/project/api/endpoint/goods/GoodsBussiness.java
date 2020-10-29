package com.telei.wms.project.api.endpoint.goods;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.wms.datasource.wms.model.Goods;
import com.telei.wms.datasource.wms.service.GoodsService;
import com.telei.wms.project.api.endpoint.goods.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import com.telei.wms.project.api.utils.TempIdGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: example 商品-接口业务
 * @Auther: leo
 * @Date: 2020/6/9 09:35
 */
@Service
public class GoodsBussiness {
    @Autowired
    private GoodsServiceCache goodsServiceCache;

    @Autowired
    private GoodsService goodsService;



    /**
     * 新增商品
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class )
    public GoodsBusinessAddResponse addGoods(GoodsBusinessAddRequest request){
        Goods goods = DataConvertUtil.parseDataAsObject(request, Goods.class);

        goods.setId(TempIdGeneratorUtil.autoId());
//        goodsServiceCache.addGoods(goods);
        goodsService.insertSelective(goods);
//        int a = 1/0;
        GoodsBusinessAddResponse businessResponse = new GoodsBusinessAddResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 修改商品
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class )
    public GoodsBusinessUpdateResponse updateGoods(GoodsBusinessUpdateRequest request){
        Goods goods = DataConvertUtil.parseDataAsObject(request, Goods.class);
        goodsServiceCache.updateGoods(goods);
        GoodsBusinessUpdateResponse businessResponse = new GoodsBusinessUpdateResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 删除商品
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class )
    public GoodsBusinessDeleteResponse deleteGoods(GoodsBusinessDeleteRequest request){
        Goods goods = DataConvertUtil.parseDataAsObject(request, Goods.class);
        goodsServiceCache.deleteGoods(goods);
        //模拟业务异常
//        ErrorCode.GOODS_ADD_ERROR_4001.throwError();
        GoodsBusinessDeleteResponse businessResponse = new GoodsBusinessDeleteResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 商品详情
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class )
    public GoodsBusinessDetailResponse detailGoods(GoodsBusinessDetailRequest request){
        Goods goods = DataConvertUtil.parseDataAsObject(request, Goods.class);
         goods = goodsServiceCache.detailGoods(goods);
        //模拟业务异常
//        ErrorCode.GOODS_ADD_ERROR_4001.throwError();
        return DataConvertUtil.parseDataAsObject(goods,GoodsBusinessDetailResponse.class);
    }

    /**
     * 商品分页查询
     * @param request
     * @return
     */
//    @DataPermissionFilter
    public GoodsBusinessPageQueryResponse pageQueryGoods(GoodsBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        Map<String, Object> parameterMap = conditionsBuilder.build();
        //这一步操作相当于将过滤参数设置进去
        parameterMap.putAll(JSON.parseObject(JSON.toJSONString(request)));
//        Pagination page = (Pagination)wmsGoodsService.selectPage(pagination,parameterMap);
        Pagination page = (Pagination) goodsService.selectCustomPage(pagination,parameterMap);
        GoodsBusinessPageQueryResponse businessResponse = new GoodsBusinessPageQueryResponse();
        businessResponse.setPage(page);
        return businessResponse;
    }

    /**
     * 商品列表
     * @param request
     * @return
     */
    public GoodsBusinessListResponse listGoods(GoodsBusinessListRequest request) {
         List<Goods> list = goodsServiceCache.listGoods();
        GoodsBusinessListResponse response = new GoodsBusinessListResponse();
        response.setList(list);
        return response;
    }
}
