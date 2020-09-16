package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.repository.WmsDoLineRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsDoLineService extends BaseService<WmsDoLineRepository,WmsDoLine,Long> {
}