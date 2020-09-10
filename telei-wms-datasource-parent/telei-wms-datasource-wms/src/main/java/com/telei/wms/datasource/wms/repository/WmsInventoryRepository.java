package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.vo.WmsInventoryPageQueryResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsInventoryRepository extends BaseRepository<WmsInventory,Long> {
    /**
     * 根据库位编码与产品id查询库存
     * @param wmsInventory
     * @return
     */
    List<WmsInventory> selectByLcCodeAndProductId(WmsInventory wmsInventory);

    int doIvSnapshotSchedule(@Param("idNumber") Long idNumber, @Param("serverNo") String serverNo, @Param("snapshotTime") String snapshotTime, @Param("snapshotLcTime") String snapshotLcTime);

    /**
     * 库存分页查询
     * @param paramMap
     * @return
     */
    List<WmsInventoryPageQueryResponseVo> selectCustomPage(Map<String, Object> paramMap);
}