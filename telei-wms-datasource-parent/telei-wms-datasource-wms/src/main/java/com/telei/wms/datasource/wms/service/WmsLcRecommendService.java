package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsLcRecommend;
import com.telei.wms.datasource.wms.repository.WmsLcRecommendRepository;
import com.telei.wms.datasource.wms.vo.LcRecommendPageQueryRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsLcRecommendService extends BaseService<WmsLcRecommendRepository,WmsLcRecommend,Long> {

    @Autowired
    private WmsLcRecommendRepository wmsLcRecommendRepository;

    public List<WmsLcRecommend> findByProductId(Long companyId, Long warehouseId, List<Long> productIds) {
        return wmsLcRecommendRepository.findByProductId(companyId, warehouseId, productIds);
    }

    public Pageable findAll(Pageable page, LcRecommendPageQueryRequestVo requestVo) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsLcRecommendRepository.findAll(requestVo);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }
}