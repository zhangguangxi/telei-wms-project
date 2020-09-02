package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.repository.WmsAdjtHeaderRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsAdjtHeaderService extends BaseService<WmsAdjtHeaderRepository,WmsAdjtHeader,Long> {
}