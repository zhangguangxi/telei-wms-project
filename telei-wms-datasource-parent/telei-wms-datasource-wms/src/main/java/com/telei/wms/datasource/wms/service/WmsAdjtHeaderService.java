package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.repository.WmsAdjtHeaderRepository;
import com.telei.wms.datasource.wms.vo.WmsAdjustHeaderPageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WmsAdjtHeaderService extends BaseService<WmsAdjtHeaderRepository,WmsAdjtHeader,Long> {
    @Autowired
    private WmsAdjtHeaderRepository repository;

    public Pageable selectCustomPage(Pagination page, Map<String, Object> paramMap) {
        PageInfo<WmsAdjustHeaderPageQueryResponseVo> pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> repository.selectCustomPage(paramMap));
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }
}