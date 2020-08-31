package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsRooHeader;
import com.telei.wms.datasource.wms.repository.WmsRooHeaderRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsRooHeaderService extends BaseService<WmsRooHeaderRepository,WmsRooHeader,Long> {
}