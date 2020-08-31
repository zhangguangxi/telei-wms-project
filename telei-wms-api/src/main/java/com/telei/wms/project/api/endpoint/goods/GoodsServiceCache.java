package com.telei.wms.project.api.endpoint.goods;

import com.alicp.jetcache.anno.*;
import com.telei.wms.datasource.wms.model.Goods;
import com.telei.wms.datasource.wms.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.telei.wms.project.api.configuration.constants.CacheNameConstant.SINGLE_WMS_GOODS_CACHE;


/**
 * @author: leo
 * @date: 2020/7/15 15:33
 */
@Service
public class GoodsServiceCache {
    @Autowired
    private GoodsService goodsService;


    /**
     * 新增商品
     *
     * @param goods
     * @return
     */
    @CacheUpdate(name = SINGLE_WMS_GOODS_CACHE, key = "#wmsGoods.id", value = "#wmsGoods")
    @CacheRefresh(refresh = 1, timeUnit = TimeUnit.MINUTES)
    public Integer addGoods(Goods goods) {
        return goodsService.insertSelective(goods);
    }

    /**
     * 修改商品
     *
     * @param goods
     * @return
     */
    @CacheUpdate(name = SINGLE_WMS_GOODS_CACHE, key = "#wmsGoods.id", value = "#wmsGoods")
    @CacheRefresh(refresh = 1, timeUnit = TimeUnit.MINUTES)
    public Integer updateGoods(Goods goods) {
        return goodsService.updateByPrimaryKeySelective(goods);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @CacheInvalidate(name = SINGLE_WMS_GOODS_CACHE, key = "#wmsGoods.id")
    @CacheRefresh(refresh = 1, timeUnit = TimeUnit.MINUTES)
    public Integer deleteGoods(Goods goods) {
        return goodsService.deleteByPrimaryKey(goods.getId());
    }

    /**
     * 获取商品详情
     * @param goods
     * @return
     */
    @Cached(name = SINGLE_WMS_GOODS_CACHE,key = "#wmsGoods.id")
    @CacheRefresh(refresh = 1,timeUnit = TimeUnit.MINUTES)
    public Goods detailGoods(Goods goods){
        return goodsService.selectByPrimaryKey(goods.getId());
    }

    /**
     * 获取商品列表
     * @return
     */
    @Cached(name = SINGLE_WMS_GOODS_CACHE,cacheType = CacheType.BOTH)
    @CacheRefresh(refresh = 1,timeUnit = TimeUnit.MINUTES)
    public List<Goods> listGoods() {
        return goodsService.selectAll();
    }
}
