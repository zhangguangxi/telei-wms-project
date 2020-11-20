package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.repository.WmsIvOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WmsIvOutService extends BaseService<WmsIvOutRepository,WmsIvOut,Long> {

    @Autowired
    private WmsIvOutRepository wmsIvOutRepository;

    public BigDecimal selectQtySum(Long productId, Long warehouseId, Long companyId) {
        return wmsIvOutRepository.selectQtySum(productId, warehouseId, companyId);
    }

    public int deleteByDolIds(List<Long> deleteIvOutList) {
        return wmsIvOutRepository.deleteByDolIds(deleteIvOutList);
    }
}