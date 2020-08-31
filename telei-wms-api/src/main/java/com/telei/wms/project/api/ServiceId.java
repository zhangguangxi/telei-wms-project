package com.telei.wms.project.api;

import com.telei.infrastructure.component.commons.annotations.RightResource;

/**
 * @Description: 服务编号  系统编号(2位) 模块编号(位) 功能编号(2位)
 * @Auther: leo
 * @Date: 2020/6/8 15:53
 */
public class ServiceId {

    /**
     * wms 子系统  03
     */

    /**
     * 商品 - demo
     * 01
     */
    //rightSystem:系统(wms,oms...)  rightItem:功能模块   rightFunction: 功能点
    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品新增")
    public static final String WMS_GOODS_ADD = "1030101";

    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品修改")
    public static final String WMS_GOODS_UPDATE = "1030102";

    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品删除")
    public static final String WMS_GOODS_DELETE = "1030103";

    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品详情")
    public static final String WMS_GOODS_DETAIL = "1030104";

    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品分页查询")
    public static final String WMS_GOODS_PAGE_QUERY = "1030105";

    @RightResource(rightSystem = "wms", rightItem = "商品", rightFunction = "商品列表")
    public static final String WMS_GOODS_LIST = "1030106";

    /**
     * 数据交互
     * 02
     */
    @RightResource(rightSystem = "wms", rightItem = "数据交互", rightFunction = "回调")
    public static final String WMS_ID_INSTANTDIRECTIVE_CALLBACK = "1030201";

    /**
     * 入库任务
     * 03
     */
    @RightResource(rightSystem = "wms", rightItem = "入库任务", rightFunction = "入库任务详细")
    public static final String WMS_RO_DETAIL = "1030301";

    @RightResource(rightSystem = "wms", rightItem = "入库任务", rightFunction = "入库任务主单分页查询")
    public static final String WMS_RO_HEADER_PAGE_QUERY = "1030302";

    @RightResource(rightSystem = "wms", rightItem = "入库任务", rightFunction = "入库任务修改主单")
    public static final String WMS_RO_HEADER_UPDATE = "1030303";




    /**
     * 收货
     * 04
     */
    @RightResource(rightSystem = "wms", rightItem = "收货单", rightFunction = "新增收货单")
    public static final String WMS_ROO_ADD = "1030401";

    @RightResource(rightSystem = "wms", rightItem = "收货单", rightFunction = "撤销收货单")
    public static final String WMS_ROO_REVOKE = "1030402";

    @RightResource(rightSystem = "wms", rightItem = "收货单", rightFunction = "收货单查询")
    public static final String WMS_ROO_PAGE_QUERY = "1030403";

    @RightResource(rightSystem = "wms", rightItem = "收货单", rightFunction = "收货单详情")
    public static final String OMS_ROO_DETAIL = "1030404";

    /**
     * 库存管理
     * 05
     */
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "上架")
    public static final String WMS_INVENTORY_PUT_ON_SHELF = "1030501";

    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "调多")
    public static final String WMS_INVENTORY_ADJUST_INCREASE = "1030502";

    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "调少")
    public static final String WMS_INVENTORY_ADJUST_REDUCE = "1030503";

    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "移库")
    public static final String WMS_INVENTORY_SHIFT = "1030504";

}
