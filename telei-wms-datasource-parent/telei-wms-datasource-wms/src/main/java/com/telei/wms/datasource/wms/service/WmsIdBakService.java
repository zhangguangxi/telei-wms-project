package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIdBak;
import com.telei.wms.datasource.wms.repository.WmsIdBakRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIdBakService extends BaseService<WmsIdBakRepository,WmsIdBak,Long> {
}