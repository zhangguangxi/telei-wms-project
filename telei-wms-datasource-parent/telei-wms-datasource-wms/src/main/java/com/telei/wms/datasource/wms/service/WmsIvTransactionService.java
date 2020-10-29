package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionRepository;
import com.telei.wms.datasource.wms.vo.WmsIvTransactionDailyKnotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WmsIvTransactionService extends BaseService<WmsIvTransactionRepository,WmsIvTransaction,Long> {

    @Autowired
    private WmsIvTransactionRepository wmsIvTransactionRepository;

    public List<WmsIvTransactionDailyKnotVO> selectByTime(Date leftTime, Date rightTime){
        return wmsIvTransactionRepository.selectByTime(leftTime, rightTime);
    }

    public List<WmsIvTransaction> selectByCreateTime(Date leftTime, Date rightTime){
        return wmsIvTransactionRepository.selectByCreateTime(leftTime, rightTime);
    }


}