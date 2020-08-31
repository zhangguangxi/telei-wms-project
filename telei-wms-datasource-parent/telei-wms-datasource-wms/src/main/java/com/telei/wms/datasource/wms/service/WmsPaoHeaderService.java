package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPaoHeader;
import com.telei.wms.datasource.wms.repository.WmsPaoHeaderRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsPaoHeaderService extends BaseService<WmsPaoHeaderRepository,WmsPaoHeader,Long> {
}