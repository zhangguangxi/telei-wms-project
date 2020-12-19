package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.repository.WmsLocationRepository;
import com.telei.wms.datasource.wms.vo.WmsLocationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WmsLocationService extends BaseService<WmsLocationRepository, WmsLocation, Long> {

    @Autowired
    private WmsLocationRepository wmsLocationRepository;


    public String getLcCodeByLocation(Long warehouseId, Integer lcCode) {
        return wmsLocationRepository.getLcCodeByLocation(warehouseId, lcCode);
    }

    public WmsLocation getCompanyLcCodeByLocation(String lcCode, Long warehouseId) {
        return wmsLocationRepository.getCompanyLcCodeByLocation(lcCode, warehouseId);
    }

    public List<WmsLocation> selectByLcCodes(List<String> list) {
        return wmsLocationRepository.selectByLcCodes(list);
    }

    public Pageable findAll(Pageable page, Map<String, Object> paramMap) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsLocationRepository.findAll(paramMap);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public List<WmsLocationVo> findAll(Map<String, Object> paramMap) {
        return wmsLocationRepository.findAll(paramMap);
    }

    public List<WmsLocation> selectLcAisleByEntity(Map<String, Object> paramMap) {
        return wmsLocationRepository.selectLcAisleByEntity(paramMap);
    }

    public List<WmsLocationVo> queryLcLocationByLcAisle(Map<String, Object> paramMap) {
        return wmsLocationRepository.queryLcLocationByLcAisle(paramMap);
    }

    public List<WmsLocationVo> queryLcLocationByLcAisleAndInventory(Map<String, Object> paramMap) {
        return wmsLocationRepository.queryLcLocationByLcAisleAndInventory(paramMap);
    }

}