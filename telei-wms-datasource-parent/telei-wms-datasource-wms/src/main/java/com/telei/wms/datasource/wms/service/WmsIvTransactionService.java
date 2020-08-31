package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvTransactionService extends BaseService<WmsIvTransactionRepository,WmsIvTransaction,Long> {
}