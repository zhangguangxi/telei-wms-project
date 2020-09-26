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

    @RightResource(rightSystem = "wms", rightItem = "入库任务", rightFunction = "更新强制收货")
    public static final String WMS_RO_HEADER_UPDATE_ENFORCEMENT = "1030304";

    @RightResource(rightSystem = "wms", rightItem = "入库任务", rightFunction = "获取强制收货状态")
    public static final String WMS_RO_HEADER_GET_ENFORCEMENT = "1030305";

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
    public static final String WMS_ROO_DETAIL = "1030404";

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
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "库存分页")
    public static final String WMS_INVENTORY_PAGE_QUERY = "1030504";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "移库")
    public static final String WMS_INVENTORY_ADJUST_SHIFT = "1030505";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "详情")
    public static final String WMS_INVENTORY_DETAIL = "1030506";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "库存调整分页")
    public static final String WMS_INVENTORY_ADJUST_PAGE_QUERY = "1030507";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "升任务")
    public static final String WMS_INVENTORY_ADJUST_LIFT_UP = "1030508";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "降任务")
    public static final String WMS_INVENTORY_ADJUST_LIFT_DOWN = "1030509";
    @RightResource(rightSystem = "wms", rightItem = "库存管理", rightFunction = "扣减")
    public static final String WMS_INVENTORY_DEDUCT = "1030510";


    /**
     * 库存初始化
     * 06
     */
    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "新增库存初始化")
    public static final String WMS_INIT_ADD = "1030601";

    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "审核库存初始化")
    public static final String WMS_INIT_AUDIT = "1030602";

    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "库存初始化查询")
    public static final String WMS_INIT_PAGE_QUERY = "1030603";

    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "库存初始化详情")
    public static final String WMS_INIT_DETAIL = "1030604";

    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "下载商品模板")
    public static final String WMS_INIT_TEMPLATE = "1030605";

    @RightResource(rightSystem = "wms", rightItem = "库存初始化", rightFunction = "检查产品库存供应商是否存在")
    public static final String WMS_INIT_CHECK = "1030606";

    /**
     * 上架单
     * 07
     */
    @RightResource(rightSystem = "wms", rightItem = "上架单", rightFunction = "新增上架单")
    public static final String WMS_PAO_ADD = "1030701";

    @RightResource(rightSystem = "wms", rightItem = "上架单", rightFunction = "上架单详细")
    public static final String WMS_PAO_DETAIL = "1030702";

    @RightResource(rightSystem = "wms", rightItem = "上架单", rightFunction = "上架单分页查询")
    public static final String WMS_PAO_PAGE_QUERY = "1030703";

    @RightResource(rightSystem = "wms", rightItem = "上架单", rightFunction = "上架单修改")
    public static final String WMS_PAO_UPDATE = "1030704";

    @RightResource(rightSystem = "wms", rightItem = "上架单", rightFunction = "取消上架单")
    public static final String WMS_PAO_CANCEL = "1030705";

    /**
     * 拣货单
     * 08
     */
    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "新增拣货单")
    public static final String WMS_PLO_ADD = "1030801";

    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "拣货单分页查询")
    public static final String WMS_PLO_HEADER_PAGE_QUERY = "1030802";

    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "拣货单详细")
    public static final String WMS_PLO_HEADER_DETAIL = "1030803";

    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "新增拣货单详情")
    public static final String WMS_PLO_ADD_DETAIL = "1030804";

    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "分页查询拣货单详情")
    public static final String WMS_PLO_DETAIL_PAGE_QUERY = "1030805";

    @RightResource(rightSystem = "wms", rightItem = "拣货单", rightFunction = "拣货完成")
    public static final String WMS_PLO_FINISH = "1030806";

    /**
     * 出库任务
     * 09
     */
    @RightResource(rightSystem = "wms", rightItem = "出库任务", rightFunction = "出库任务详细")
    public static final String WMS_DO_DETAIL = "1030901";

    @RightResource(rightSystem = "wms", rightItem = "出库任务", rightFunction = "出库任务主单分页查询")
    public static final String WMS_DO_HEADER_PAGE_QUERY = "1030902";

    @RightResource(rightSystem = "wms", rightItem = "出库任务", rightFunction = "核验出库任务")
    public static final String WMS_DO_VERIFICATION = "1030903";

    @RightResource(rightSystem = "wms", rightItem = "出库任务", rightFunction = "出库")
    public static final String WMS_DO_SHIP = "1030904";

    /**
     * 升降任务
     * 10
     */
    @RightResource(rightSystem = "wms", rightItem = "升降任务", rightFunction = "新增升降任务")
    public static final String WMS_LIFT_WORK_ADD = "1031001";

    @RightResource(rightSystem = "wms", rightItem = "升降任务", rightFunction = "撤销升降任务")
    public static final String WMS_LIFT_WORK_REVOKE = "1031002";

    @RightResource(rightSystem = "wms", rightItem = "升降任务", rightFunction = "升降任务查询")
    public static final String WMS_LIFT_WORK_PAGE_QUERY = "1031003";

    @RightResource(rightSystem = "wms", rightItem = "升降任务", rightFunction = "升降任务处理")
    public static final String WMS_LIFT_WORK_UPDATE = "1031004";

    @RightResource(rightSystem = "wms", rightItem = "升降任务", rightFunction = "查询产品对应库位信息")
    public static final String WMS_LIFT_WORK_INFO = "1031005";

}
