package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInitHeader;
import com.telei.wms.datasource.wms.repository.WmsInitHeaderRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsInitHeaderService extends BaseService<WmsInitHeaderRepository,WmsInitHeader,Long> {
}