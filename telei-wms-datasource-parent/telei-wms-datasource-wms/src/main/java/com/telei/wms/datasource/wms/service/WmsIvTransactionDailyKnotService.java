package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionDailyKnotRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvTransactionDailyKnotService extends BaseService<WmsIvTransactionDailyKnotRepository,WmsIvTransactionDailyKnot,Long> {
}