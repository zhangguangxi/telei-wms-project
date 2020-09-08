package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.repository.WmsInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsInventoryService extends BaseService<WmsInventoryRepository,WmsInventory,Long> {
    @Autowired
    private WmsInventoryRepository wmsInventoryRepository;
    public List<WmsInventory> selectByLcCodeAndProductId(WmsInventory wmsInventory) {
        return wmsInventoryRepository.selectByLcCodeAndProductId(wmsInventory);
    }
}