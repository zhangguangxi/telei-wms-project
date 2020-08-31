package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.repository.WmsInventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsInventoryService extends BaseService<WmsInventoryRepository,WmsInventory,Long> {
}