package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.repository.WmsLocationRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsLocationService extends BaseService<WmsLocationRepository,WmsLocation,Long> {
}