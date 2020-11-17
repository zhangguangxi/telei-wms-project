package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsBacklcList;
import com.telei.wms.datasource.wms.repository.WmsBacklcListRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsBacklcListService extends BaseService<WmsBacklcListRepository,WmsBacklcList,Long> {
}