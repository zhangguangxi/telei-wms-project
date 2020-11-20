package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WmsIvOutRepository extends BaseRepository<WmsIvOut,Long> {

    /**
     * 获取待出库存数
     * @param productId
     * @param warehouseId
     * @param companyId
     * @return
     */
    BigDecimal selectQtySum(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("companyId") Long companyId);

    /**
     * 根据出库任务明细id删除出库记录
     * @param deleteIvOutList
     * @return
     */
    int deleteByDolIds(List<Long> deleteIvOutList);
}