package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.vo.LiftTaskPageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.WmsInventoryPageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.WmsInventoryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface WmsInventoryRepository extends BaseRepository<WmsInventory, Long> {
    /**
     * 根据库位编码与产品id查询库存
     *
     * @param wmsInventory
     * @return
     */
    List<WmsInventory> selectByCustomEntity(WmsInventory wmsInventory);

    void doIvSnapshotSchedule(@Param("idNumber") Long idNumber, @Param("serverNo") String serverNo, @Param("snapshotTime") String snapshotTime, @Param("snapshotLcTime") String snapshotLcTime);

    /**
     * 库存分页查询
     *
     * @param paramMap
     * @return
     */
    List<WmsInventoryPageQueryResponseVo> selectCustomPage(Map<String, Object> paramMap);

    /**
     * 智能升降配置分页查询
     *
     * @param paramMap
     * @return
     */
    List<LiftTaskPageQueryResponseVo> liftTaskPageQuery(Map<String, Object> paramMap);

    /**
     * 获取库存数
     *
     * @param productId
     * @param warehouseId
     * @param companyId
     * @return
     */
    BigDecimal selectQtySum(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("companyId") Long companyId);

    /**
     * 就近查找当前商品的高架库位
     * @param productId
     * @param warehouseId
     * @param companyId
     * @param lcCodeNumber
     * @return
     */
    WmsInventoryVo getLcCodeByInventory(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("companyId") Long companyId, @Param("lcCodeNumber") Integer lcCodeNumber);

}