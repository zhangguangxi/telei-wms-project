package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSplit;
import com.telei.wms.datasource.wms.repository.WmsIvSplitRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvSplitService extends BaseService<WmsIvSplitRepository,WmsIvSplit,Long> {
}