package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.repository.WmsDoHeaderRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsDoHeaderService extends BaseService<WmsDoHeaderRepository,WmsDoHeader,Long> {
}