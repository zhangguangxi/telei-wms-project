package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.repository.WmsDoHeaderRepository;
import com.telei.wms.datasource.wms.repository.WmsRooHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WmsDoHeaderService extends BaseService<WmsDoHeaderRepository,WmsDoHeader,Long> {

    @Autowired
    private WmsDoHeaderRepository wmsDoHeaderRepository;

    public Pageable findAll(Pageable page, Map<String, Object> paramMap) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsDoHeaderRepository.findAll(paramMap);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

}