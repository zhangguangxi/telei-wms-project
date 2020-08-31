package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsRoHeader;
import com.telei.wms.datasource.wms.repository.WmsRoHeaderRepository;
import com.telei.wms.datasource.wms.vo.RoHeaderPageQueryRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WmsRoHeaderService extends BaseService<WmsRoHeaderRepository, WmsRoHeader,Long> {

    @Autowired
    private WmsRoHeaderRepository wmsRoHeaderRepository;

    public Pageable findAll(Pageable page, RoHeaderPageQueryRequestVo requestVo) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsRoHeaderRepository.findAll(requestVo);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }
}