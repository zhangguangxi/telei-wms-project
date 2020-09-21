package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvOutConfirm;
import com.telei.wms.datasource.wms.repository.WmsIvOutConfirmRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvOutConfirmService extends BaseService<WmsIvOutConfirmRepository,WmsIvOutConfirm,Long> {
}