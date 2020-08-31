package com.telei.wms.project.api.endpoint.goods.dto;

import com.telei.wms.datasource.wms.model.Goods;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/7/16 16:01
 */
@Data
public class GoodsListResponse {
    private List<Goods> list;
}
