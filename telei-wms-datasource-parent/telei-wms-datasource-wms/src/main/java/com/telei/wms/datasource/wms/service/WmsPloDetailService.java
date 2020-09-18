package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPloDetail;
import com.telei.wms.datasource.wms.repository.WmsPloDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsPloDetailService extends BaseService<WmsPloDetailRepository,WmsPloDetail,Long> {
}