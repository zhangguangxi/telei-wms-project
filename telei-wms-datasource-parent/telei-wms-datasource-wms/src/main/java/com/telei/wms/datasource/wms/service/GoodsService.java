package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.Goods;
import com.telei.wms.datasource.wms.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GoodsService extends BaseService<GoodsRepository, Goods,Long> {
    @Autowired
    private GoodsRepository goodsRepository;

    public Pageable selectCustomPage(Pageable page, Map<String, Object> conditions) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> goodsRepository.selectByCustomConditions(conditions));
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }
}