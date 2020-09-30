package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsProductSample;
import com.telei.wms.datasource.wms.repository.WmsProductSampleRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsProductSampleService extends BaseService<WmsProductSampleRepository,WmsProductSample,Long> {
}