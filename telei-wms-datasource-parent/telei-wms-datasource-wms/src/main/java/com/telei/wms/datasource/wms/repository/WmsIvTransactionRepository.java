package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.datasource.wms.vo.WmsIvTransactionDailyKnotVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WmsIvTransactionRepository extends BaseRepository<WmsIvTransaction,Long> {

    List<WmsIvTransactionDailyKnotVO> selectByTime(@Param("leftTime") Date leftTime, @Param("rightTime") Date rightTime);

}