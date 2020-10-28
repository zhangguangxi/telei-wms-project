package com.telei.wms.schedule.task.service;

import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.datasource.wms.model.Goods;
import com.telei.wms.datasource.wms.repository.GoodsRepository;
import com.telei.wms.datasource.wms.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Description: example 商品-接口业务
 * @Auther: leo
 * @Date: 2020/6/9 09:35
 */
@Service
public class GoodsBussiness {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private Id idGenerator;


    /**
     * 新增商品
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class )
    public void addGoods(){
        Goods goods = new Goods();
        goods.setName("aa");
        goods.setId(idGenerator.getUnique());
        goodsService.insertSelective(goods);
//        int a = 1/0;;
    }
}
