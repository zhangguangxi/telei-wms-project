package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.vo.WmsLocationVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsLocationRepository extends BaseRepository<WmsLocation,Long> {

    String getLcCodeByLocation(@Param("warehouseId") Long warehouseId, @Param("lcCode") Integer lcCode);

    WmsLocation getCompanyLcCodeByLocation(@Param("lcCode") String lcCode, @Param("warehouseId") Long warehouseId);

    List<WmsLocation> selectByLcCodes(List<String> list);

    List<WmsLocationVo> findAll(Map<String, Object> paramMap);

    List<WmsLocation> selectLcAisleByEntity(Map<String, Object> paramMap);

    List<WmsLocationVo> queryLcLocationByLcAisle(Map<String, Object> paramMap);

}