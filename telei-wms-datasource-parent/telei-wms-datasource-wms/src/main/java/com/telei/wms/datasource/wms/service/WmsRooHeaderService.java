package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsRooHeader;
import com.telei.wms.datasource.wms.repository.WmsRooHeaderRepository;
import com.telei.wms.datasource.wms.vo.ReportVo;
import com.telei.wms.datasource.wms.vo.RooHeaderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WmsRooHeaderService extends BaseService<WmsRooHeaderRepository,WmsRooHeader,Long> {

    @Autowired
    private WmsRooHeaderRepository wmsRooHeaderRepository;

    public Pageable findAll(Pageable page, Map<String, Object> paramMap) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsRooHeaderRepository.findAll(paramMap);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public RooHeaderResponseVo selectRooHeaderDetail(Long id) {
        return wmsRooHeaderRepository.selectRooHeaderDetail(id);
    }

    public Pageable rooReportQuery(Pageable page, Map<String, Object> paramMap) {
        PageInfo<ReportVo> pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> wmsRooHeaderRepository.rooReportQuery(paramMap));
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public List<ReportVo> rooReportExport(Map<String, Object> paramMap) {
        return wmsRooHeaderRepository.rooReportQuery(paramMap);
    }

}