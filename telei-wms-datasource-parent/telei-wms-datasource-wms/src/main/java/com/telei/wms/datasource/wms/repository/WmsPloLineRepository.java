package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPloLine;
import com.telei.wms.datasource.wms.vo.PloLineLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PloLinePageQueryResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsPloLineRepository extends BaseRepository<WmsPloLine,Long> {

    List<PloLinePageQueryResponseVo> findAll(Long ploId);

    List<PloLineLocationResponseVo> findLocationAll(@Param("warehouseId") Long warehouseId, @Param("productIds") List<Long> productIds);
}