package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPaoLine;
import com.telei.wms.datasource.wms.vo.PaoLineLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PaoLinePageQueryResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsPaoLineRepository extends BaseRepository<WmsPaoLine,Long> {

    List<PaoLinePageQueryResponseVo> findAll(@Param("paoId") Long paoId, @Param("companyId") Long companyId);

    List<PaoLineLocationResponseVo> findLocationAll(@Param("warehouseId") Long warehouseId, @Param("productIds") List<Long> productIds);
}