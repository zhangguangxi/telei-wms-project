package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionDailyKnotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WmsIvTransactionDailyKnotService extends BaseService<WmsIvTransactionDailyKnotRepository,WmsIvTransactionDailyKnot,Long> {
    @Autowired
    private WmsIvTransactionDailyKnotRepository wmsIvTransactionDailyKnotRepository;

    public Pageable selectCustomPage(Pageable page, Map<String, Object> conditions) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(),page.getPageSize()).doSelectPageInfo(()->wmsIvTransactionDailyKnotRepository.selectByCustomConditions(conditions));
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public List<WmsIvTransactionDailyKnot> selectByCustomCondtions(Map conditions) {
        return wmsIvTransactionDailyKnotRepository.selectByCustomCondtions(conditions);
    }
}