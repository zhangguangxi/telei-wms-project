package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.IdBak;
import com.telei.wms.datasource.wms.repository.IdBakRepository;
import org.springframework.stereotype.Service;

@Service
public class IdBakService extends BaseService<IdBakRepository,IdBak,Integer> {
}