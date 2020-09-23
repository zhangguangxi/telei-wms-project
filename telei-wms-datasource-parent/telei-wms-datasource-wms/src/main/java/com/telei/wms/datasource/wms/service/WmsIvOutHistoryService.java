package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvOutHistory;
import com.telei.wms.datasource.wms.repository.WmsIvOutHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvOutHistoryService extends BaseService<WmsIvOutHistoryRepository,WmsIvOutHistory,Long> {
}