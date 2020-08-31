package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIdException;
import com.telei.wms.datasource.wms.repository.WmsIdExceptionRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIdExceptionService extends BaseService<WmsIdExceptionRepository,WmsIdException,Long> {
}