package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsIvTransactionDailyKnotRepository extends BaseRepository<WmsIvTransactionDailyKnot,Long> {
    List<WmsIvTransactionDailyKnot> selectByCustomConditions(Map<String, Object> conditions);

    List<WmsIvTransactionDailyKnot> selectByCustomCondtions(Map conditions);
}