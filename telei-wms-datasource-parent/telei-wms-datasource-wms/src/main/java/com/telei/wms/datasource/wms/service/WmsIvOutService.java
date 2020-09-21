package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.repository.WmsIvOutRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvOutService extends BaseService<WmsIvOutRepository,WmsIvOut,Long> {
}