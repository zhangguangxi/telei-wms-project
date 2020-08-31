package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPaoLine;
import com.telei.wms.datasource.wms.repository.WmsPaoLineRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsPaoLineService extends BaseService<WmsPaoLineRepository,WmsPaoLine,Long> {
}