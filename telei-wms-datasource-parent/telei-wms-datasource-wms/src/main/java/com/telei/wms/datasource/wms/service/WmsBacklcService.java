package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsBacklc;
import com.telei.wms.datasource.wms.repository.WmsBacklcRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsBacklcService extends BaseService<WmsBacklcRepository,WmsBacklc,Long> {
}