package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.Goods;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GoodsRepository extends BaseRepository<Goods,Long> {
    /**
     * 根据条查询查询商品列表
     * @param conditions
     * @return
     */
    List<Goods> selectByCustomConditions(Map conditions);
}