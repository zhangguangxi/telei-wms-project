package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.repository.WmsInventoryRepository;
import com.telei.wms.datasource.wms.vo.WmsInventoryPageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WmsInventoryService extends BaseService<WmsInventoryRepository,WmsInventory,Long> {
    @Autowired
    private WmsInventoryRepository wmsInventoryRepository;

    public List<WmsInventory> selectByCustomEntity(WmsInventory wmsInventory) {
        return wmsInventoryRepository.selectByCustomEntity(wmsInventory);
    }

    public Pageable selectCustomPage(Pagination page, Map<String, Object> paramMap) {
        PageInfo<WmsInventoryPageQueryResponseVo> pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> wmsInventoryRepository.selectCustomPage(paramMap));
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public BigDecimal selectQtySum(Long productId, Long warehouseId, Long companyId) {
        return wmsInventoryRepository.selectQtySum(productId, warehouseId, companyId);
    }
}