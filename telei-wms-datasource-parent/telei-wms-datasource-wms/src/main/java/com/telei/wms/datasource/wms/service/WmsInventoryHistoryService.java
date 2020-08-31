package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInventoryHistory;
import com.telei.wms.datasource.wms.repository.WmsInventoryHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsInventoryHistoryService extends BaseService<WmsInventoryHistoryRepository,WmsInventoryHistory,Long> {
}