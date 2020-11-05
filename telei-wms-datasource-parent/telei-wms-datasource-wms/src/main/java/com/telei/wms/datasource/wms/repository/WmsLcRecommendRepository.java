package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsLcRecommend;
import com.telei.wms.datasource.wms.vo.LcRecommendPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.LcRecommendPageQueryResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsLcRecommendRepository extends BaseRepository<WmsLcRecommend,Long> {

    List<WmsLcRecommend> findByProductId(@Param("companyId") Long companyId, @Param("warehouseId") Long warehouseId, @Param("productIds") List<Long> productIds);

    List<LcRecommendPageQueryResponseVo> findAll(LcRecommendPageQueryRequestVo requestVo);
}