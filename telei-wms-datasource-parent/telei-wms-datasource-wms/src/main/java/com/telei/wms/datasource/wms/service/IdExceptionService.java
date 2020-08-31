package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.IdException;
import com.telei.wms.datasource.wms.repository.IdExceptionRepository;
import org.springframework.stereotype.Service;

@Service
public class IdExceptionService extends BaseService<IdExceptionRepository,IdException,Integer> {
}