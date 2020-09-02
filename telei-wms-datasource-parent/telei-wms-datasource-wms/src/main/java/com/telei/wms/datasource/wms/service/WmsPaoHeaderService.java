package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPaoHeader;
import com.telei.wms.datasource.wms.repository.WmsPaoHeaderRepository;
import com.telei.wms.datasource.wms.vo.PaoHeaderPageQueryRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WmsPaoHeaderService extends BaseService<WmsPaoHeaderRepository,WmsPaoHeader,Long> {

    @Autowired
    private WmsPaoHeaderRepository wmsPaoHeaderRepository;

    public Pageable findAll(Pageable page, PaoHeaderPageQueryRequestVo requestVo) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsPaoHeaderRepository.findAll(requestVo);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }
}