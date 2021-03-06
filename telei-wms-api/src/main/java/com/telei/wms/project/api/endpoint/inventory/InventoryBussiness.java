package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.commons.utils.CollectorsUtil;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.inventoryAddWriteBack.OmsInventoryAddWriteBack;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.customer.amqp.ivOutWriteBack.OmsIvOutWriteBack;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.customer.product.dto.ProductRequest;
import com.telei.wms.customer.recovicePlan.dto.RecovicePlanAddByDoRequest;
import com.telei.wms.customer.warehouse.WarehouseFeignClient;
import com.telei.wms.customer.warehouse.dto.WarehouseDetailRequest;
import com.telei.wms.customer.warehouse.dto.WarehouseDetailResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.WmsDeductIvOutConfirmResponseVo;
import com.telei.wms.datasource.wms.vo.WmsInventoryDeductConditionVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryAddWriteBackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryChangeWriteBackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsIvOutWriteBackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsRecovicePlanAddByDoProducer;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
import com.telei.wms.project.api.endpoint.inventory.strategy.*;
import com.telei.wms.project.api.endpoint.lcRecommend.LcRecommendBussiness;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendDeleteRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telei.wms.commons.utils.DateUtils.RANGE_TIME_END;
import static com.telei.wms.commons.utils.DateUtils.RANGE_TIME_START;
import static java.util.stream.Collectors.*;

/**
 * @author: leo
 * @date: 2020/8/26 09:35
 */
@Slf4j
@Service
public class InventoryBussiness {
    //部分入库
    private static  final  String PART_INVENTORY_STATUS = "40";

    //已入库
    private static final String ALL_IN_INVENTORY_STATUS ="50";

    //已出库
    private static final String ALL_OUT_INVENTORY_STATUS ="40";

    //已上架
    private static final String ALL_SHIFT_INVENTORY_STATUS ="40";

    //库存调整-审核状态
    private static  final String ADJUST_REVIEW = "20";

    //已上架-状态
    private static  final String SHIFT_STATUS = "20";

    //拣货完成
    private static  final String PICK_FINISHED ="30";

    //已取消
    private static final String ALREADY_CANCELLED = "98";

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsRoHeaderService wmsRoHeaderService;

    @Autowired
    private WmsRoLineService wmsRoLineService;

    @Autowired
    private WmsRooHeaderService wmsRooHeaderService;

    @Autowired
    private WmsPaoHeaderService wmsPaoHeaderService;

    @Autowired
    private WmsPaoLineService wmsPaoLineService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private WmsIvAttributebatchService wmsIvAttributebatchService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsInventoryHistoryService wmsInventoryHistoryService;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    @Autowired
    private WmsOmsInventoryAddWriteBackProducer wmsOmsInventoryAddWriteBackProducer;

    @Autowired
    private WmsOmsInventoryChangeWriteBackProducer wmsOmsInventoryChangeWriteBackProducer;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private WmsAdjtHeaderService wmsAdjtHeaderService;

    @Autowired
    private WmsAdjtLineService wmsAdjtLineService;

    @Autowired
    private WmsIvTransactionService wmsIvTransactionService;

    @Autowired
    private WmsIvSplitService wmsIvSplitService;

    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private WmsDoLineService wmsDoLineService;


    @Autowired
    private WmsIvOutService wmsIvOutService;

    @Autowired
    private WmsIvOutConfirmService wmsIvOutConfirmService;

    @Autowired
    private WmsPloLineService wmsPloLineService;

    @Autowired
    private WmsOmsIvOutWriteBackProducer wmsOmsIvOutWriteBackProducer;

    @Autowired
    private WmsLocationService wmsLocationService;

    @Autowired
    private WmsIvTransactionDailyKnotService wmsIvTransactionDailyKnotService;

    @Autowired
    private LcRecommendBussiness lcRecommendBussiness;

    @Autowired
    private WmsPloHeaderService wmsPloHeaderService;

    @Autowired
    private WmsPloDetailService wmsPloDetailService;

    @Autowired
    private WarehouseFeignClient warehouseFeignClient;

    @Autowired
    private WmsDoContainerService wmsDoContainerService;

    @Autowired
    private DeductStrategyFactory deductStrategyFactory;

    @Autowired
    private WmsOmsRecovicePlanAddByDoProducer wmsOmsRecovicePlanAddByDoProducer;

    /**
     * 入库(上架)
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryAddBussinessResponse addInventory(InventoryAddBussinessRequest request) {
        log.info("\n +++++++++++++++++++++ 上架操作::入参 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(request));
        List<InventoryAddBussinessRequest.InventoryAddRequestCondition> requestList = request.getList();
        if (requestList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        //1.基本信息提取
        //1.1 基本信息提取-上架单单头id集合
        /**库位编码*/

        List<Long> requestPaoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaoId).collect(toList());
        if (Objects.isNull(requestPaoIdList) || requestPaoIdList.isEmpty() || (requestPaoIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020.throwError();
        }
        //1.2 基本信息提取-上架单明细id集合
        List<Long> requestPaolIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId).collect(toList());
        if (Objects.isNull(requestPaolIdList) || requestPaolIdList.isEmpty() || (requestPaolIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006.throwError();
        }
        //1.3 基本信息提取-库存批次id集合
        List<Long> requestIabIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIabId).collect(toList());
        if (Objects.isNull(requestIabIdList) || requestIabIdList.isEmpty() || (requestIabIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004.throwError();
        }
        //1.4 基本信息提取-收货单单头id集合
        List<Long> requestRooIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId).collect(toList());
        if (Objects.isNull(requestRooIdList) || requestRooIdList.isEmpty() || (requestRooIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }
        //1.5 基本信息提取-收货单明细集合
        List<Long> requestRooLIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoolId).collect(toList());
        if (Objects.isNull(requestRooLIdList) || requestRooLIdList.isEmpty() || (requestRooLIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021.throwError();
        }
        //1.6 基本信息提取-入库任务单头id集合
        List<Long> requestRoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId).collect(toList());
        if (Objects.isNull(requestRoIdList) || requestRoIdList.isEmpty() || (requestRoIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010.throwError();
        }
        //1.8 基本信息提取-产品id集合
        List<Long> requestProductIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getProductId).collect(toList());
        if (Objects.isNull(requestProductIdList) || requestProductIdList.isEmpty() || (requestProductIdList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002.throwError();
        }
        //1.9.1 基本信息提取-输入参数:库位编码
        List<String> requestLcCodeList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getLcCode).collect(toList());
        if (Objects.isNull(requestLcCodeList) || requestLcCodeList.isEmpty() || (requestLcCodeList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_LC_CODE_IS_NULL_4022.throwError();
        }
        //1.9.1 基本信息提取-输入参数:本次收货数(库存数)
        List<BigDecimal> requestIvQtyList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty).collect(toList());
        if (Objects.isNull(requestIvQtyList) || requestIvQtyList.isEmpty() || (requestIvQtyList.size() != requestList.size())) {
            ErrorCode.INVENTORY_ADD_ERROR_IV_QTY_IS_NULL_4023.throwError();
        }

        //获取锁-相关单据单头作为参数(上架单单头、库存批次单头、收货单单头、入库单头);
        List<Long> lockKey = Stream.of(requestPaoIdList, requestIabIdList, requestRoIdList, requestRoIdList).flatMap(Collection::stream).collect(toList());
        Object lockValue = LockMapUtil.tryLock(lockKey);
        log.info("\n +++++++++++++++++++++ 上架操作::尝试获取锁，lockKey ->{},lockValue -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(lockKey), String.valueOf(lockValue));
        InventoryAddBussinessResponse response = new InventoryAddBussinessResponse();
        try {
            //2公用信息提取
            //2.1 UTC时间
            Date nowWithUTC = DateUtils.nowWithUTC();
            //2.2 用户信息(请求上下文)
            UserInfo userInfo = CustomRequestContext.getUserInfo();
            Long companyId = request.getList().get(0).getCompanyId();
            Long warehouseId = request.getList().get(0).getWarehouseId();

            //3、预处理
            //3.0 库位-预处理

            List<WmsLocation> wmsLocationList = wmsLocationService.selectByLcCodes(requestLcCodeList);
            if(Objects.isNull(wmsLocationList) || wmsLocationList.isEmpty()){
                ErrorCode.INVENTORY_ADD_ERROR_LC_CODE_NOT_EXIST_24.throwError(JSON.toJSONString(requestLcCodeList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据库位编码 -> {},查询库位集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestLcCodeList), JSON.toJSONString(wmsLocationList));
            Set<String> lcCodeList = wmsLocationList.stream().map(WmsLocation::getLcCode).collect(toSet());
            Set<String> notExistLcCodeList = requestLcCodeList.stream().filter(item -> !lcCodeList.contains(item)).collect(toSet());
            if(Objects.nonNull(notExistLcCodeList) && !notExistLcCodeList.isEmpty()){
                ErrorCode.INVENTORY_ADD_ERROR_LC_CODE_NOT_EXIST_24.throwError(JSON.toJSONString(notExistLcCodeList));
            }


            //3.1 产品(商品)-预处理
            ProductRequest productDetailRequest = new ProductRequest();
            productDetailRequest.setProductIds(requestProductIdList);
            productDetailRequest.setCompanyId(companyId);
            ApiResponse productListResponse = productFeignClient.getProductList(productDetailRequest);
            ProductListResponse productResponse = JSON.parseObject(JSON.toJSONString(productListResponse.getData()), ProductListResponse.class);
            List<ProductDetailResponse> productList = productResponse.getProductList();
            if (Objects.isNull(productList) || productList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003.throwError(JSON.toJSONString(requestProductIdList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据产品IDS -> {},查询产品集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestProductIdList), JSON.toJSONString(productList));

            //3.2 样品库位-预处理
            Set<Long> productIdSet = productList.stream().map(ProductDetailResponse::getProductId).collect(toSet());
            Map<Long, String> productId2ProductNameMap = productList.stream().collect(toMap(ProductDetailResponse::getProductId, ProductDetailResponse::getProductName));
            Set<String> requestLcCodeSet = requestLcCodeList.stream().collect(toSet());

            ConditionsBuilder inventoryConditionMap = ConditionsBuilder.create();
            inventoryConditionMap.eq("companyId",companyId);
            inventoryConditionMap.in("warehouseId",Arrays.asList(warehouseId));
            inventoryConditionMap.in("productId",productIdSet);

            Map<String, Object> inventoryParamMap = inventoryConditionMap.build();
            List<WmsInventory> inventories = wmsInventoryService.selectByConditions(inventoryParamMap);
            if(Objects.nonNull(inventories) && !inventories.isEmpty()){
                Map<String, List<InventoryAddBussinessRequest.InventoryAddRequestCondition>> frontProductGroup = requestList.stream().collect(groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId())));
                Map<String, List<WmsInventory>> dbProductIdGroupMap = inventories.stream().collect(groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId())));
                frontProductGroup.forEach((k,v)->{
                    if(dbProductIdGroupMap.containsKey(k)){
                        Set<String> dbLcCodeSet = dbProductIdGroupMap.get(k).stream().filter(item ->item.getLcCode().toLowerCase().startsWith("s")).map(WmsInventory::getLcCode).collect(toSet());
                        if(dbLcCodeSet.size() > 1){
                            String productId = k.split("#")[2];
                            ErrorCode.INVENTORY_ADD_ERROR_EXIST_MULTI_SAMPLE_LC_CODE_4057.throwError(JSON.toJSONString(productId2ProductNameMap.get(Long.valueOf(productId))),JSON.toJSONString(dbLcCodeSet));
                        }
                    }
                });
                Map<Long, String> requestProductId2LcCode = requestList.stream().collect(toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getProductId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getLcCode));
                List<WmsInventory> sampleWmsInventoryList = inventories.stream().filter(item -> item.getLcCode().toLowerCase().startsWith("s")).collect(toList());
                if(Objects.nonNull(sampleWmsInventoryList) && !sampleWmsInventoryList.isEmpty()){
                    Map<Long, String> dbProductId2LcCodeMap = sampleWmsInventoryList.stream().collect(toMap(WmsInventory::getProductId, WmsInventory::getLcCode,(v1,v2)-> v2));
                    requestProductId2LcCode.forEach((k,v)->{
                        if(dbProductId2LcCodeMap.containsKey(k)){
                            if(!dbProductId2LcCodeMap.get(k).equalsIgnoreCase(v)){
                                ErrorCode.INVENTORY_ADD_ERROR_EXIST_MULTI_SAMPLE_LC_CODE_4058.throwError(JSON.toJSONString(productId2ProductNameMap.get(Long.valueOf(k))),JSON.toJSONString(dbProductId2LcCodeMap.get(k)));
                            }
                        }
                    });
                }

            }

            //2.2 库存批次-预处理
            List<WmsIvAttributebatch> ivAttributebatcheList = wmsIvAttributebatchService.selectByPrimaryKeys(requestIabIdList);
            if (Objects.isNull(ivAttributebatcheList) || ivAttributebatcheList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005.throwError(JSON.toJSONString(requestIabIdList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据库存批次IDS -> {},查询库存批次集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestIabIdList), JSON.toJSONString(ivAttributebatcheList));

            //2.3 上架单单头-预处理(上架单单头 上架时间、上架用户、已上架数量)
            InventoryAddBussinessRequest.InventoryAddRequestCondition requestConditionItem = requestList.get(0);
            Long paoId = requestConditionItem.getPaoId();

            WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(paoId);
            if (Objects.isNull(wmsPaoHeader)) {
                ErrorCode.INVENTORY_ADD_ERROR_PAO_NOT_EXIST_40025.throwError(paoId);
            }

            log.info("\n +++++++++++++++++++++ 上架操作::根据上架单单头ID -> {},查询上架单单头记录 -> {} ++++++++++++++++++++ \n ", paoId, JSON.toJSONString(wmsPaoHeader));
            wmsPaoHeader.setPutawayUser(requestConditionItem.getPutawayUser());//上架员
            wmsPaoHeader.setPutawayTime(nowWithUTC);//上架时间
            BigDecimal waitPutawayQty = requestList.stream().collect(CollectorsUtil.summingBigDecimal(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
            wmsPaoHeader.setPutawayQty(Objects.isNull(wmsPaoHeader.getPutawayQty()) ? waitPutawayQty : (wmsPaoHeader.getPutawayQty().add(waitPutawayQty)));//已上架数量
            wmsPaoHeader.setLastupdateUser(userInfo.getUserName());//用户名
            wmsPaoHeader.setLastupdateTime(nowWithUTC);//最后更新时间
            wmsPaoHeader.setPaoStatus(SHIFT_STATUS);//已上架

            //2.4 上架单明细-预处理(上架时间、上架用户、上架数量)
            List<WmsPaoLine> paoLineList = wmsPaoLineService.selectByPrimaryKeys(requestPaolIdList);
            if (Objects.isNull(paoLineList) || paoLineList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError(JSON.toJSONString(requestPaolIdList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据上架明细IDS -> {},查询上架单明细集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestPaolIdList), JSON.toJSONString(paoLineList));

            List<Long> shiftList = paoLineList.stream().filter(item -> Objects.nonNull(item.getPaoStatus()) && Objects.equals(SHIFT_STATUS, item.getPaoStatus())).map(WmsPaoLine::getPaoId).collect(toList());
            if(Objects.nonNull(shiftList) && !shiftList.isEmpty()){
                    ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_ALREADY_SHIFT_23.throwError(JSON.toJSONString(shiftList));
            }
            List<Long> productIds = new ArrayList<>();
            Map<Long, InventoryAddBussinessRequest.InventoryAddRequestCondition> paolId2EntityMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId, Function.identity()));
            paoLineList.stream().forEach(item -> {
                productIds.add(item.getProductId());
                item.setPaolQty(paolId2EntityMap.get(item.getId()).getIvQty());
                item.setPutawayTime(nowWithUTC);
                item.setPutawayUser(userInfo.getUserName());
                item.setPaoStatus(SHIFT_STATUS);
                item.setLcCode(paolId2EntityMap.get(item.getId()).getLcCode());
            });

            //2.5 收货单单头-预处理(更新 putawayQty)
            Map<Long, BigDecimal> rooQtyMap = requestList.stream().collect(Collectors.groupingBy(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId, CollectorsUtil.summingBigDecimal(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty)));
            if (Objects.isNull(rooQtyMap) || rooQtyMap.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
            }

            List<WmsRooHeader> rooHeaderList = wmsRooHeaderService.selectByPrimaryKeys(requestRooIdList);
            if (Objects.isNull(rooHeaderList) || rooHeaderList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008.throwError(JSON.toJSONString(requestRooIdList));
            }

            log.info("\n +++++++++++++++++++++ 上架操作::根据收货单单头IDS -> {},查询上收货单单头集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestRooIdList), JSON.toJSONString(rooHeaderList));
            rooHeaderList.forEach(item -> {
                item.setPutawayQty(Objects.isNull(item.getPutawayQty()) ? rooQtyMap.get(item.getId()) : item.getPutawayQty().add(rooQtyMap.get(item.getId())));
                item.setRoStatus(ALL_SHIFT_INVENTORY_STATUS);
            });

            //2.6 入库任务单头-预处理(上架数量累加:putawayQty 上架完成时间:putawayAllTime(when=putawayQty>=totalQty)  总计划入库数量:totalQty)
            Map<Long, BigDecimal> roQtyMap = requestList.stream().collect(Collectors.groupingBy(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId,
                    CollectorsUtil.summingBigDecimal(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty)));


            //入库任务单头
            List<WmsRoHeader> roHeaderList = wmsRoHeaderService.selectByPrimaryKeys(requestRoIdList);
            if (Objects.isNull(roHeaderList) || roHeaderList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError(JSON.toJSONString(requestRoIdList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据上架单单头IDS -> {},查询上架单单头集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestRoIdList), JSON.toJSONString(roHeaderList));

            roHeaderList.stream().forEach(item -> {
                item.setPutawayQty(Objects.isNull(item.getPutawayQty()) ? roQtyMap.get(item.getId()) : item.getPutawayQty().add(roQtyMap.get(item.getId())));//上架数量
                if (item.getTotalQty().compareTo(item.getPutawayQty()) > 0) {
                    item.setOrderStatus(PART_INVENTORY_STATUS);//部分入库
                    return;
                }
                item.setPutawayAllTime(nowWithUTC);
                item.setOrderStatus(ALL_IN_INVENTORY_STATUS);//已入库
            });

            //2.7 oms-入库计划单(单头/明细)-预处理 oms-采购单(单头/明细)-预处理
            List<Long> rolIdList = paoLineList.stream().map(WmsPaoLine::getRolId).collect(toList());//计划明细id集合
            if (Objects.isNull(rolIdList) || rolIdList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012.throwError();
            }

            List<WmsRoLine> roLineList = wmsRoLineService.selectByPrimaryKeys(rolIdList);
            if (Objects.isNull(roLineList) || roLineList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013.throwError(JSON.toJSONString(rolIdList));
            }
            log.info("\n +++++++++++++++++++++ 上架操作::根据入库计划明细IDS -> {},查询入库计划明细集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(requestRooIdList), JSON.toJSONString(rooHeaderList));

            Map<Long, WmsPaoLine> paoLineRolIdToEntityMap = paoLineList.stream().collect(Collectors.toMap(WmsPaoLine::getRolId, Function.identity()));
            List<OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition> omsInventoryAddWriteBackConditionList = new ArrayList<>();
            roLineList.stream().forEach(item -> {
                OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition omsInventoryAddWriteBackCondition = new OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition();
                omsInventoryAddWriteBackCondition.setCompanyId(wmsPaoHeader.getCompanyId());//公司id
                omsInventoryAddWriteBackCondition.setWarehouseId(wmsPaoHeader.getWarehouseId());//仓库id
                omsInventoryAddWriteBackCondition.setRpId(item.getRpId());//入库计划id
                omsInventoryAddWriteBackCondition.setRpdId(item.getRpdId());//入库计划明细id
                omsInventoryAddWriteBackCondition.setPoId(item.getPoId());//采购id
                omsInventoryAddWriteBackCondition.setPodId(item.getPodId());//采购明细id
                omsInventoryAddWriteBackCondition.setProductId(item.getProductId());//产品id
                omsInventoryAddWriteBackCondition.setIvQty(paoLineRolIdToEntityMap.get(item.getId()).getPaolQty());
                omsInventoryAddWriteBackConditionList.add(omsInventoryAddWriteBackCondition);
            });
            OmsInventoryAddWriteBack omsInventoryAddWriteBack = new OmsInventoryAddWriteBack();
            omsInventoryAddWriteBack.setOrderType(request.getOrderType());
            omsInventoryAddWriteBack.setList(omsInventoryAddWriteBackConditionList);

            //2.8 库存记录-预处理
            Map<Long, ProductDetailResponse> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, Function.identity()));
            Map<Long, WmsIvAttributebatch> ivAttributeBatchMap = ivAttributebatcheList.stream().collect(Collectors.toMap(WmsIvAttributebatch::getId, Function.identity()));
            List<WmsInventory> inventoryList = DataConvertUtil.parseDataAsArray(requestList, WmsInventory.class);
            inventoryList.stream().forEach(inventory -> {
                WmsIvAttributebatch ivAttributebatchDetail = ivAttributeBatchMap.get(inventory.getIabId());
                ProductDetailResponse productDetail = productMap.get(inventory.getProductId());
                BigDecimal ivQty = inventory.getIvQty();
                BigDecimal bigBagRate = new BigDecimal(ivAttributebatchDetail.getBigBagRate());//大包转转换率(批次表记录)
                BigDecimal midBagRate = new BigDecimal(ivAttributebatchDetail.getMidBagRate());//中包转化数
                inventory.setIvId(idGenerator.getUnique());
                inventory.setProductDate(ivAttributebatchDetail.getProductDate());//生产日期
                inventory.setIabId(ivAttributebatchDetail.getId());//批次id
                inventory.setApCodeDc("RECV");//引起本次变动库存变动的来源单据引用类型
                inventory.setQsCode("GD");//实物类别
                inventory.setIvDocumentCode(wmsPaoHeader.getPaoCode());//业务单据编码
                inventory.setIvFifoTime(ivAttributebatchDetail.getCreateTime());//先进先出时间(获取跑批次表的创建时间)
                inventory.setStockUnit(productDetail.getStockUnit());//计量单位
                inventory.setBigBagRate(Objects.isNull(bigBagRate) ? 0 : bigBagRate.intValue());//大包转换率  批次表中获取
                inventory.setBigBagQty(ivQty.divideAndRemainder(bigBagRate)[0]);//大包数量
                inventory.setBigBagExtraQty(ivQty.divideAndRemainder(bigBagRate)[1]);//大包剩余数量
                inventory.setMidBagRate(Objects.isNull(midBagRate) ? 0 : midBagRate.intValue()); // 中包转化数(批次表记录)
                inventory.setMidBagQty(ivQty.divideAndRemainder(midBagRate)[0]);//中包数量
                inventory.setMidBagExtraQty(ivQty.divideAndRemainder(midBagRate)[1]);//中包剩余数量
                inventory.setIvTranstime(nowWithUTC);//最近库存更新时间
                inventory.setIvCreatetime(nowWithUTC);//创建时间
                inventory.setBizDate(nowWithUTC);//业务日期(与创建时间保持一致)
                inventory.setIvLocksign(0);//库存锁，可以出货
                inventory.setIvFreezesign(0);//东解锁，可以操作
                inventory.setLcType("S");//库位类型,S:样品库位  Z:高架库位
            });
            //3、处理(状态数据变更)
            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                inventoryDataProcess(inventoryList, wmsPaoHeader, paoLineList, rooHeaderList, roHeaderList, omsInventoryAddWriteBack, userInfo, nowWithUTC);
                //删除推荐库位埋点
                LcRecommendDeleteRequest lcRecommendDeleteRequest = new LcRecommendDeleteRequest();
                lcRecommendDeleteRequest.setCompanyId(wmsPaoHeader.getCompanyId());
                lcRecommendDeleteRequest.setWarehouseId(wmsPaoHeader.getWarehouseId());
                lcRecommendDeleteRequest.setOrderCode(wmsPaoHeader.getPaoCode());
                lcRecommendDeleteRequest.setProductIds(productIds);
                lcRecommendBussiness.deleteLcRecommend(lcRecommendDeleteRequest);
            }else{
                response.setIsSuccess(false);
            }
        } finally {
            LockMapUtil.cancelLock(lockKey, lockValue);
        }
        return response;
    }

    /**
     * 入库相关数据处理
     *
     * @param inventoryList
     * @param wmsPaoHeader
     * @param paoLineList
     * @param rooHeaderList
     * @param roHeaderList
     * @param omsInventoryAddWriteBack
     */
    private void inventoryDataProcess(List<WmsInventory> inventoryList, WmsPaoHeader wmsPaoHeader, List<WmsPaoLine> paoLineList, List<WmsRooHeader> rooHeaderList,
                                      List<WmsRoHeader> roHeaderList, OmsInventoryAddWriteBack omsInventoryAddWriteBack, UserInfo userInfo, Date nowWithUTC) {
        //3.1 库存/库存历史处理  插入wms_inventory wms_inventory_history wms_iv_transaction
        log.info("\n +++++++++++++++++++++ 上架操作::新增库存记录  -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(inventoryList));
        int countForInventory = wmsInventoryService.insertBatch(inventoryList);
        if (countForInventory <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4014.throwError();
        }
        List<WmsInventoryHistory> inventoryHistoryList = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventoryHistory.class);
        log.info("\n +++++++++++++++++++++ 上架操作::新增库存历史记录  -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(inventoryHistoryList));
        int countForInventoryHistory = wmsInventoryHistoryService.insertBatch(inventoryHistoryList);
        if (countForInventoryHistory <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4015.throwError();
        }

        Map<Long, WmsInventory> ivId2EntityMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getId, Function.identity()));
        List<WmsIvTransaction> wmsIvTransactions = DataConvertUtil.parseDataAsArray(inventoryList, WmsIvTransaction.class);
        wmsIvTransactions.stream().forEach(item -> {
            WmsInventory inventory = ivId2EntityMap.get(item.getIvId());
            item.setCreateUser(userInfo.getUserName());//创建用户
            item.setCreateTime(nowWithUTC);//创建时间
            item.setApCode(inventory.getApCodeDc());//应用类型编码
            item.setIvtChangeType("INCR");//变动类型
            item.setIvQtyTo(inventory.getIvQty());//调整后数量
            item.setIvQtyFrom(inventory.getIvQty());//调整前数量
            item.setLcCodeFrom(inventory.getLcCode());//库位编码
            item.setLcCodeTo(inventory.getLcCode());//变动后库位编码
            item.setIvtDocumentCode(inventory.getIvDocumentCode());//引起库存变动业务单据号
            item.setIvtDocumentId(inventory.getIvDocumentId());//引起库存变动业务单据id
            item.setIvtDocumentlineId(inventory.getIvDocumentlineId());//引起库存变动业务单据明细id
            item.setDcQty(inventory.getIvQty());//调整数量
        });
        log.info("\n +++++++++++++++++++++ 上架操作::新增库存变动记录  -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsIvTransactions));
        int countForTransaction = wmsIvTransactionService.insertBatch(wmsIvTransactions);
        if (countForTransaction <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4023.throwError();
        }

        //3.2 更新上架单单头/上架单明细
        log.info("\n +++++++++++++++++++++ 上架操作::更新上架单单头 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsPaoHeader));
        int countForPao = wmsPaoHeaderService.updateByPrimaryKeySelective(wmsPaoHeader);
        if (countForPao <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4016.throwError();
        }
        log.info("\n +++++++++++++++++++++ 上架操作::更新上架单明细 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(paoLineList));
        int countForPaoLine = wmsPaoLineService.updateBatch(paoLineList);
        if (countForPaoLine <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4017.throwError();
        }
        //2.2 更新收货单单头
        log.info("\n +++++++++++++++++++++ 上架操作::更新收货单单头， -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(rooHeaderList));
        int countForRootHeader = wmsRooHeaderService.updateBatch(rooHeaderList);
        if (countForRootHeader <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4018.throwError();
        }
        //2.3 更新入库任务单头
        log.info("\n +++++++++++++++++++++ 上架操作::更新入库任务， -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(roHeaderList));
        int countForRoHeader = wmsRoHeaderService.updateBatch(roHeaderList);
        if (countForRoHeader <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4019.throwError();
        }

        //4、异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)
        //4.1 指令入库
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsInventoryAddWriteBackProducer.getQueueName(), "", omsInventoryAddWriteBack);
        //4.2 MQ发送指令
        log.info("\n +++++++++++++++++++++ 上架操作::异步回写OMS单据 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(omsInventoryAddWriteBack));
        wmsOmsInventoryAddWriteBackProducer.send(wmsIdInstantdirective);
    }

    /**
     * 库存-调增
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryIncreaseBussinessResponse increaseInventory(InventoryIncreaseBussinessRequest request) {
        InventoryIncreaseBussinessResponse response = new InventoryIncreaseBussinessResponse();
        response.setIsSuccess(adjustInventory(request, "INCREASE"));
        return response;
    }


    /***
     * 库存-调少
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryReduceBussinessResponse reduceInventory(InventoryReduceBussinessRequest request) {
        InventoryReduceBussinessResponse response = new InventoryReduceBussinessResponse();
        response.setIsSuccess(adjustInventory(request, "REDUCE"));
        return response;
    }


    /**
     * 移库
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryShiftBussinessResponse shiftInventory(InventoryShiftBussinessRequest request) {
        InventoryShiftBussinessResponse response = new InventoryShiftBussinessResponse();
        response.setIsSuccess(adjustInventory(request, "MOVE"));;
        return response;
    }

    /**
     * 库存调整
     *
     * @param request
     * @param adjhType
     */
    public boolean adjustInventory(Object request, String adjhType) {
        log.info("\n +++++++++++++++++++++ 库存调整::入参 -> {},调整类型 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(request),adjhType);
        /**基础信息*/
        Date nowWithUTC = DateUtils.nowWithUTC();//UTC时间
        Long companyId = Long.valueOf(String.valueOf(((Map) JSON.toJSON(request)).get("companyId")));
        Long warehouseId = Long.valueOf(String.valueOf(((Map) JSON.toJSON(request)).get("warehouseId")));
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        long adjtId = idGenerator.getUnique();//库存调整单单头id
        String bussinessNumber = getBussinessNumber("TZ");//获取业务单号

        /**库存调整单单头*/
        WmsAdjtHeader wmsAdjtHeader = DataConvertUtil.parseDataAsObject(request, WmsAdjtHeader.class);
        ProductRequest productRequest = new ProductRequest();
        Long productId = Long.valueOf(String.valueOf(((Map) JSON.toJSON(request)).get("productId")));
        productRequest.setCompanyId(userInfo.getCompanyId());
        productRequest.setProductIds(Arrays.asList(productId));
        ApiResponse apiResponse = productFeignClient.getProductList(productRequest);
        List<ProductDetailResponse> productDetailResponseList = DataConvertUtil.parseDataAsArray(((Map) apiResponse.getData()).get("productList"), ProductDetailResponse.class);
        if (Objects.isNull(productDetailResponseList) || productDetailResponseList.isEmpty()) {
            ErrorCode.ADJT_ERROR_4001.throwError(wmsAdjtHeader.getProductBarcode(), adjhType);
        }
        ProductDetailResponse productDetail = productDetailResponseList.get(0);
        log.info("\n +++++++++++++++++++++ 库存调整::根据产品ID -> {},获取产品详情 -> {} ++++++++++++++++++++ \n ", productId, productDetail);

        wmsAdjtHeader.setProductName(productDetail.getProductName());//产品名称
        wmsAdjtHeader.setProductBarcode(productDetail.getProductBarcode());//条码
        wmsAdjtHeader.setProductNameLocal(productDetail.getProductNameLocal());//产品本地名称
        wmsAdjtHeader.setProductUpcCode(productDetail.getProductUpcCode());//UPC码z
        wmsAdjtHeader.setAdjhCode(bussinessNumber);//业务单据编码
        wmsAdjtHeader.setId(adjtId);
        wmsAdjtHeader.setBizDate(nowWithUTC);//业务日期
        wmsAdjtHeader.setCreateTime(nowWithUTC);//创建时间
        wmsAdjtHeader.setAuditTime(nowWithUTC);//审核时间
        wmsAdjtHeader.setCreateUser(userInfo.getUserName());//创建用户
        wmsAdjtHeader.setAuditUser(userInfo.getUserName());// 审核用户
        wmsAdjtHeader.setAdjhType(adjhType);//调整类型
        wmsAdjtHeader.setIvihStatus(ADJUST_REVIEW);//库存调整状态

        /**库存记录*/
        WmsInventory wmsInventory = new WmsInventory();

        String lcCode = wmsAdjtHeader.getLcCode();
        wmsInventory.setCompanyId(companyId);
        wmsInventory.setWarehouseId(warehouseId);
        wmsInventory.setLcCode(lcCode);
        wmsInventory.setProductId(productId);
        List<WmsInventory> wmsInventories = wmsInventoryService.selectByCustomEntity(wmsInventory);
        if (Objects.isNull(wmsInventories) || wmsInventories.isEmpty()) {
            ErrorCode.ADJT_ERROR_4002.throwError(lcCode, productId, adjhType);
        }
        log.info("\n +++++++++++++++++++++ 库存调整::公司ID -> {},仓库ID -> {},库位ID -> {},产品ID,获取库存集合 -> {} ++++++++++++++++++++ \n ", companyId, warehouseId, lcCode, productId, JSON.toJSONString(wmsInventories));

        Object lockKey = adjtId;
        Object lockValue = LockMapUtil.tryLock(lockKey);
        log.info("\n +++++++++++++++++++++ 库存调整::尝试获取锁 当前线程-> {},lockKey -> {},lockValue -> {} ++++++++++++++++++++ \n ", Thread.currentThread().getName(), lockKey, lockValue);
        try {
            List<WmsAdjtLine> adjtLineList = Lists.newArrayList();
            List<WmsInventory> inventoryAddList = Lists.newArrayList();
            List<WmsInventory> inventoryUpdateList = Lists.newArrayList();
            List<WmsIvTransaction> ivTransactionList = Lists.newArrayList();
            List<WmsIvSplit> ivSplitsList = Lists.newArrayList();
            List<Long> deleteIvidList = Lists.newArrayList();
            IAdjustStrategy adjustStrategy = adjustStrategyFactory.getAdjustStrategy(adjhType);
            /**库存调整-预处理(数据组装)*/
            List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition>
                    omsInventoryChangeWriteBackConditions = adjustStrategy.process(wmsAdjtHeader, adjtLineList, wmsInventories,
                    inventoryAddList, inventoryUpdateList,
                    deleteIvidList, ivTransactionList, ivSplitsList, userInfo
                    , nowWithUTC);
            /**库存调整-落库处理*/
            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                log.info("\n +++++++++++++++++++++ 库存调整::获取锁的当前线程 -> {},lockKey -> {},lockValue -> {} ++++++++++++++++++++ \n ", Thread.currentThread().getName(), lockKey, lockValue);
                dataProcess(adjhType, wmsAdjtHeader, productId, lcCode, adjtLineList, inventoryAddList, inventoryUpdateList, ivTransactionList, ivSplitsList, deleteIvidList, omsInventoryChangeWriteBackConditions);
                return true;
            }
            return false;
        } finally {
            log.info("\n +++++++++++++++++++++ 库存调整::释放锁,当前线程 -> {},lockKey -> {},lockValue -> {} ++++++++++++++++++++ \n ", Thread.currentThread().getName(), lockKey, lockValue);
            LockMapUtil.cancelLock(lockKey, lockValue);
        }
    }

    private void dataProcess(String adjhType, WmsAdjtHeader wmsAdjtHeader, Long productId, String lcCode, List<WmsAdjtLine> adjtLineList, List<WmsInventory> inventoryAddList, List<WmsInventory> inventoryUpdateList, List<WmsIvTransaction> ivTransactionList, List<WmsIvSplit> ivSplitsList, List<Long> deleteIvidList, List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> omsInventoryChangeWriteBackConditions) {
        /**数据状态处理*/
        /**库存调整单单头*/
        log.info("\n +++++++++++++++++++++ 库存调整::新增库存调整单单头 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsAdjtHeader));
        int countForAdjHeader = wmsAdjtHeaderService.insertSelective(wmsAdjtHeader);
        if (countForAdjHeader <= 0) {
            ErrorCode.ADJT_ERROR_4003.throwError(lcCode, productId, adjhType);
        }
        /**库存调整单明细*/
        log.info("\n +++++++++++++++++++++ 库存调整:: 新增库存调整单明细 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(adjtLineList));
        int countForAdjtLine = wmsAdjtLineService.insertBatch(adjtLineList);
        if (countForAdjtLine <= 0) {
            ErrorCode.ADJT_ERROR_4005.throwError(lcCode, productId, adjhType);
        }
        /**库存记录*/
        if (!inventoryAddList.isEmpty()) {
            log.info("\n +++++++++++++++++++++ 库存调整:: 新增库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(inventoryAddList));
            int countForInventoryAdd = wmsInventoryService.insertBatch(inventoryAddList);
            if (countForInventoryAdd <= 0) {
                ErrorCode.ADJT_ERROR_4006.throwError(lcCode, productId, adjhType);
            }
            /**库存历史记录*/
            List<WmsInventoryHistory> wmsInventoryHistoryAddList = DataConvertUtil.parseDataAsArray(inventoryAddList, WmsInventoryHistory.class);
            log.info("\n +++++++++++++++++++++ 库存调整:: 新增库存历史记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsInventoryHistoryAddList));
            int countForInventoryHistoryAdd = wmsInventoryHistoryService.insertBatch(wmsInventoryHistoryAddList);
            if (countForInventoryHistoryAdd <= 0) {
                ErrorCode.ADJT_ERROR_4007.throwError(lcCode, productId, adjhType);
            }
        }

        /**库存修改记录*/
        if (!inventoryUpdateList.isEmpty()) {
            log.info("\n +++++++++++++++++++++ 库存调整:: 更新库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(inventoryUpdateList));
            int countForInventoryUpdate = wmsInventoryService.updateBatch(inventoryUpdateList);
            if (countForInventoryUpdate <= 0) {
                ErrorCode.ADJT_ERROR_4008.throwError(lcCode, productId, adjhType);
            }
        }

        /**库存删除记录*/
        if (!deleteIvidList.isEmpty()) {
            log.info("\n +++++++++++++++++++++ 库存调整:: 删除库存记录，库存ID集合 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(deleteIvidList));
            int countForInventoryDelete = wmsInventoryService.deleteByPrimaryKeys(deleteIvidList);
            if (countForInventoryDelete <= 0) {
                //库存调整失败-库存记录删除失败，库位编码={0},产品id={1},调整类型={2}
                ErrorCode.ADJT_ERROR_4009.throwError(lcCode, productId, adjhType);
            }
        }

        /**库存变更记录*/
        if (ivTransactionList.isEmpty()) {
            ErrorCode.ADJT_ERROR_4010.throwError(lcCode, productId, adjhType);
        }
        log.info("\n +++++++++++++++++++++ 库存调整:: 新增库存变更记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(ivTransactionList));

        int countForIvTransactionAdd = wmsIvTransactionService.insertBatch(ivTransactionList);
        if (countForIvTransactionAdd <= 0) {
            ErrorCode.ADJT_ERROR_4011.throwError(lcCode, productId, adjhType);
        }

        /**库存拆分记录*/
        if (!ivSplitsList.isEmpty()) {
            log.info("\n +++++++++++++++++++++ 库存调整:: 新增库存拆分记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(ivSplitsList));
            int countForSplitAdd = wmsIvSplitService.insertBatch(ivSplitsList);
            if (countForSplitAdd <= 0) {
                ErrorCode.ADJT_ERROR_4012.throwError(lcCode, productId, adjhType);
            }
        }

        // 异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)
        // 指令入库
        if (Objects.nonNull(omsInventoryChangeWriteBackConditions) && !omsInventoryChangeWriteBackConditions.isEmpty()) {
            OmsInventoryChangeWriteBack omsInventoryChangeWriteBack = new OmsInventoryChangeWriteBack();
            omsInventoryChangeWriteBack.setList(omsInventoryChangeWriteBackConditions);
            WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsInventoryChangeWriteBackProducer.getQueueName(), "", omsInventoryChangeWriteBack);
            // MQ发送指令
            log.info("\n +++++++++++++++++++++ 库存调整::  异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)-> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsIdInstantdirective));
            wmsOmsInventoryChangeWriteBackProducer.send(wmsIdInstantdirective);
        }
    }


    /***
     * 获取业务单号
     * @param systemType
     * @return
     */
    private String getBussinessNumber(String systemType) {
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType(systemType);
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        String businessNumber = businessNumberResponse.getBusinessNumber();
        if (StringUtils.isEmpty(businessNumber)) {
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        log.info("\n +++++++++++++++++++++ 上架操作::获取业务单据号 -> {} ++++++++++++++++++++ \n ", businessNumber);
        return businessNumber;
    }

    /**
     * 库存分页查询
     *
     * @param request
     * @return
     */
    public InventoryPageQueryBussinessResponse pageQueryInventory(InventoryPageQueryBussinessRequest request) {
        Pagination pagination = new Pagination(request.getPageCommonRequest().getPageNumber(), request.getPageCommonRequest().getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getLcCode())) {
            conditionsBuilder.like("lcCode", request.getInventoryPageQueryCondition().getLcCode());
        }
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductNo())) {
            conditionsBuilder.like("productNo", request.getInventoryPageQueryCondition().getProductNo());
        }

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductName())) {
            conditionsBuilder.like("productName", request.getInventoryPageQueryCondition().getProductName());
        }

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getInventoryPageQueryCondition().getProductBarcode());
        }
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getLcType())) {
            conditionsBuilder.eq("lcType", request.getInventoryPageQueryCondition().getLcType());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        conditionsBuilder.eq("warehouseId", request.getInventoryPageQueryCondition().getWarehouseId());
        conditionsBuilder.orderBy(" cp.product_no DESC");
        Map<String, Object> paramMap = conditionsBuilder.build();
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getHasLcType())) {
            paramMap.put("hasLcType", request.getInventoryPageQueryCondition().getHasLcType());
        }
        Pagination page = (Pagination) wmsInventoryService.selectCustomPage(pagination, paramMap);
        InventoryPageQueryBussinessResponse response = new InventoryPageQueryBussinessResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 库存详情
     *
     * @param request
     * @return
     */
    public InventoryDetailBussinessResponse detailInventory(InventoryDetailBussinessRequest request) {
        log.info("\n +++++++++++++++++++++ 库存分布-查看库存详情::入参 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(request));
        WmsInventory wmsInventory = DataConvertUtil.parseDataAsObject(request, WmsInventory.class);
        List<WmsInventory> wmsInventories = wmsInventoryService.selectByCustomEntity(wmsInventory);
        if (Objects.isNull(wmsInventories) || wmsInventories.isEmpty()) {
            ErrorCode.INVENTORY_DETAIL_ERROR_4024.throwError(request.getLcCode(), request.getProductId());
        }
        List<InventoryDetailResponse.InventoryDetailCondition> list = Lists.newArrayList();
        Map<Long, List<WmsInventory>> iabToInventoryMap = wmsInventories.stream().collect(Collectors.groupingBy(WmsInventory::getIabId));
        if(Objects.isNull(iabToInventoryMap) || iabToInventoryMap.isEmpty()){
            ErrorCode.INVENTORY_DETAIL_ERROR_40259.throwError(JSON.toJSONString(wmsInventories.stream().map(WmsInventory::getId).collect(Collectors.toList())));
        }
        Set<Long> iabIdSet = iabToInventoryMap.keySet();
        List<WmsIvAttributebatch> ivAttributeBatchList = wmsIvAttributebatchService.selectByPrimaryKeys(iabIdSet);
        if(Objects.isNull(ivAttributeBatchList) || ivAttributeBatchList.isEmpty()){
            ErrorCode.INVENTORY_DETAIL_ERROR_40260.throwError(JSON.toJSONString(iabIdSet));
        }
        Map<Long, WmsIvAttributebatch> id2EntityMap = ivAttributeBatchList.stream().collect(toMap(WmsIvAttributebatch::getId, Function.identity()));
        iabToInventoryMap.forEach((k, v) -> {
            InventoryDetailResponse.InventoryDetailCondition detailVo = new InventoryDetailResponse.InventoryDetailCondition();
            detailVo.setIabId(k);
            // 根据库存批次查询批次创建时间
            WmsIvAttributebatch attributebatch = id2EntityMap.get(k);
            detailVo.setCreateTime(attributebatch.getCreateTime());
            BigDecimal iabQty = v.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
            WmsInventory dbInventory = v.get(0);
            Integer bigBagRate = dbInventory.getBigBagRate();
            Integer midBagRate = dbInventory.getMidBagRate();
            //大包数量
            BigDecimal[] bigDecimals = iabQty.divideAndRemainder(new BigDecimal(bigBagRate));
            detailVo.setBigBagQty(bigDecimals[0]);
            BigDecimal[] midDecimals = bigDecimals[1].divideAndRemainder(new BigDecimal(midBagRate));
            detailVo.setMidBagQty(midDecimals[0]);
            detailVo.setTinyBagQty(midDecimals[1]);
            detailVo.setQty(iabQty);
            list.add(detailVo);
        });
        list.sort((o1,o2)-> o2.getCreateTime().compareTo(o1.getCreateTime()));
        InventoryDetailBussinessResponse response = new InventoryDetailBussinessResponse();
        response.setList(list);
        return response;
    }

    /**
     * 库存调整分页
     *
     * @param request
     * @return
     */
    public InventoryAdjustPageQueryBussinessResponse pageQueryAdjustInventory(InventoryAdjustPageQueryBussinessRequest request) {
        Pagination pagination = new Pagination(request.getPageCommonRequest().getPageNumber(), request.getPageCommonRequest().getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getLcCode())) {
            conditionsBuilder.like("lcCode", request.getInventoryPageQueryCondition().getLcCode());
        }
        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductNo())) {
            conditionsBuilder.like("productNo", request.getInventoryPageQueryCondition().getProductNo());
        }

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductName())) {
            conditionsBuilder.like("productName", request.getInventoryPageQueryCondition().getProductName());
        }

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getInventoryPageQueryCondition().getProductBarcode());
        }

        if(StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getAdjhType())){
            conditionsBuilder.eq("adjhType",request.getInventoryPageQueryCondition().getAdjhType());
        }

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getLcType())) {
            conditionsBuilder.eq("lcType", request.getInventoryPageQueryCondition().getLcType());
        }

        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());

        conditionsBuilder.orderBy("wa.create_time desc");
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsAdjtHeaderService.selectCustomPage(pagination, paramMap);
        InventoryAdjustPageQueryBussinessResponse response = new InventoryAdjustPageQueryBussinessResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 升任务
     *
     * @param request
     * @return
     */
    @Transactional
    public InventoryLiftUpBussinessResponse liftUpInventory(InventoryLiftUpBussinessRequest request) {
        InventoryLiftUpBussinessResponse response = new InventoryLiftUpBussinessResponse();
        response.setIsSuccess(adjustInventory(request, "LIFTUP"));
        return response;
    }

    /**
     * 降任务
     *
     * @param request
     * @return
     */
    @Transactional
    public InventoryLiftDownBussinessResponse liftDownInventory(InventoryLiftDownBussinessRequest request) {
        InventoryLiftDownBussinessResponse response = new InventoryLiftDownBussinessResponse();
        response.setIsSuccess(adjustInventory(request, "LIFTDOWN"));
        return response;
    }

    /**
     * 扣减库存
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryDeductBussinessResponse deductInventory(InventoryDeductBussinessRequest request) {
        log.info("\n +++++++++++++++++++++ 出库扣减库存操作::入参 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(request));
        Long companyId = request.getCompanyId();
        Long warehouseId = request.getWarehouseId();
        Long dohId = request.getDohId();//出库任务id(订单id),库存锁key
        String orderType = request.getOrderType();
        Object lockValue = LockMapUtil.tryLock(dohId);
        try {
            WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(dohId);
            if (Objects.isNull(wmsDoHeader)) {
                ErrorCode.INVENTORY_DEDUCT_DO_RECORD_NOT_EXIST_40026.throwError();
            }
            log.info("\n +++++++++++++++++++++ 出库扣减库存操作::根据出库任务单头ID -> {}，查询出库任务单头记录 ++++++++++++++++++++ \n ", JSON.toJSONString(dohId), JSON.toJSONString(wmsDoHeader));
            /**判断是否生成拣货单*/
            if (Objects.equals("0", wmsDoHeader.getHasPlo())) {
                ErrorCode.INVENTORY_DEDUCT_NOT_HAS_PLO_40027.throwError(JSON.toJSONString(dohId));
            }
            /**判断是否生成核验单*/
            if (Objects.equals("0", wmsDoHeader.getHadCheck())) {
                ErrorCode.INVENTORY_DEDUCT_NOT_HAD_CHECK_40028.throwError(JSON.toJSONString(dohId));
            }
            /**判断对应的拣货状态是否 "拣货完成"*/
            ConditionsBuilder ploHeaderCondition = ConditionsBuilder.create();
            ploHeaderCondition.eq("dohId",dohId);
            ploHeaderCondition.ne("orderStatus",ALREADY_CANCELLED);
            WmsPloHeader ploHeader = wmsPloHeaderService.selectOneByConditions(ploHeaderCondition.build());
            if(Objects.isNull(ploHeader)){
                ErrorCode.INVENTORY_DEDUCT_PLO_HEADER_IS_NULL_40048.throwError(JSON.toJSONString(dohId));
            }
            if(!ploHeader.getOrderStatus().equalsIgnoreCase(PICK_FINISHED)){
                ErrorCode.INVENTORY_DEDUCT_PLO_NOT_FINISHED_40049.throwError(JSON.toJSONString(dohId));
            }

            /**查询出库任务明细*/
            WmsDoLine wmsDoLine = new WmsDoLine();
            wmsDoLine.setDohId(dohId);
            List<WmsDoLine> wmsDoLines = wmsDoLineService.selectByEntity(wmsDoLine);
            if (Objects.isNull(wmsDoLines) || wmsDoLines.isEmpty()) {
                ErrorCode.INVENTORY_DEDUCT_DO_LINE_NOT_EXIST_40029.throwError(JSON.toJSONString(dohId));
            }
            log.info("\n +++++++++++++++++++++ 出库扣减库存操作::根据出库任务单头ID -> {}，查询出库任务明细记录 ++++++++++++++++++++ \n ", JSON.toJSONString(dohId), JSON.toJSONString(wmsDoLines));
            /**根据订单id(出库任务id)查询wms_iv_out(待出库存-出库计划审核时生成)集合*/
            WmsIvOut wmsIvOut = new WmsIvOut();
            wmsIvOut.setOrderId(dohId);
            List<WmsIvOut> wmsIvOutList = wmsIvOutService.selectByEntity(wmsIvOut);
            log.info("\n +++++++++++++++++++++ 出库扣减库存操作::根据订单id -> {} 查询待出库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(dohId), JSON.toJSONString(wmsIvOutList));
            if (Objects.isNull(wmsIvOutList) || wmsIvOutList.isEmpty()) {
                ErrorCode.INVENTORY_DEDUCT_IV_OUT_NOT_EXIST_40030.throwError(JSON.toJSONString(wmsIvOut));
            }
            /**获取待出库存集合中的lineId(出库任务明细id)集合,即出库任务明细id */
            Set<Long> dolIdList = wmsIvOutList.stream().map(WmsIvOut::getLineId).collect(toSet());
            /**查询拣货单明细(详情)*/
            List<WmsPloLine> wmsPloLineList = wmsPloLineService.selectByDolIdSet(dolIdList);
            log.info("\n +++++++++++++++++++++ 出库扣减库存操作::根据出库任务详情IDS -> {} 查询拣货详情记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(dolIdList), JSON.toJSONString(wmsPloLineList));
            if (Objects.isNull(wmsPloLineList) || wmsPloLineList.isEmpty()) {
                ErrorCode.INVENTORY_DEDUCT_PLO_LINE_NOT_EXIST_40031.throwError(JSON.toJSONString(wmsPloLineList));
            }
            /**判断是否拣货单数量  = 退库数量 + 装箱上数量*/
            BigDecimal ploQty = Objects.isNull(wmsDoHeader.getPloQty())?BigDecimal.ZERO:wmsDoHeader.getPloQty();
            BigDecimal shipQty = ploQty;
            WarehouseDetailRequest warehouseDetailRequest = new WarehouseDetailRequest();
            warehouseDetailRequest.setCompanyId(companyId);
            ApiResponse warehouseResponse = warehouseFeignClient.getCompanyWarehouse(warehouseDetailRequest);
            WarehouseDetailResponse detailResponse = JSON.parseObject(JSON.toJSONString(warehouseResponse.getData()), WarehouseDetailResponse.class);
            if(Objects.isNull(detailResponse)){
                ErrorCode.INVENTORY_DEDUCT_COMPANY_DEFAULT_WAREHOUSE_NOT_EXIST_40054.throwError(JSON.toJSONString(companyId));
            }
            String containerType = detailResponse.getContainerType();
            if(StringUtils.equalsIgnoreCase(containerType,"Y")){
                BigDecimal backlcQty = Objects.isNull(wmsDoHeader.getBacklcQty())?BigDecimal.ZERO:wmsDoHeader.getBacklcQty();//退库数量
                BigDecimal containerQty = Objects.isNull(wmsDoHeader.getContainerQty())?BigDecimal.ZERO:wmsDoHeader.getContainerQty();//装柜数量
                if(!(ploQty.compareTo(backlcQty.add(containerQty)) == 0)){
                    ErrorCode.INVENTORY_DEDUCT_EXIST_PROCESS_PRODUCT_40055.throwError(JSON.toJSONString(companyId));
                }
                shipQty = containerQty;//装柜数量
            }
            /**捡货单明细id->lcCode映射*/
            Set<WmsPloLine> wmsPloLineSet = wmsPloLineList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WmsPloLine::getDolId))), HashSet::new)
            );
            Map<Long, String> dolId2LcCodeMap = wmsPloLineSet.stream().collect(Collectors.toMap(WmsPloLine::getDolId, WmsPloLine::getLcCode));
            List<String> requestLcCodeList = wmsPloLineList.stream().map(WmsPloLine::getLcCode).collect(toList());
            /**样品库位校验*/
            Set<String>  nonSampleLocations = Sets.newHashSet();
            requestLcCodeList.forEach(item ->{
                    if(!item.toLowerCase().startsWith("s")){
                        nonSampleLocations.add(item);
                    }
            });
            if(!nonSampleLocations.isEmpty()){
                ErrorCode.INVENTORY_DEDUCT_NONE_SAMPLE_LC_CODE_40050.throwError(JSON.toJSONString(nonSampleLocations));
            }
            /**产品校验*/
            List<Long> requestProductList = wmsPloLineList.stream().map(WmsPloLine::getProductId).collect(toList());
            ProductRequest productDetailRequest = new ProductRequest();
            productDetailRequest.setProductIds(requestProductList);
            productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            ApiResponse productListResponse = productFeignClient.getProductList(productDetailRequest);
            ProductListResponse productFeignResponse = JSON.parseObject(JSON.toJSONString(productListResponse.getData()), ProductListResponse.class);
            List<ProductDetailResponse> productList = productFeignResponse.getProductList();
            if (Objects.isNull(productList) || productList.isEmpty()) {
                ErrorCode.INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40047.throwError(JSON.toJSONString(requestProductList));
            }
            Map<Long, String> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, ProductDetailResponse::getProductName));
            /**拣货单( productId_lcCode -> pickedQty)*/
            Map<String, BigDecimal>  groupByProductIdAndLcCodeMapForDeduct = wmsPloLineList.stream().collect(Collectors.groupingBy((item -> item.getProductId() + "#" + item.getLcCode()), CollectorsUtil.summingBigDecimal(WmsPloLine::getPickedQty)));
            /**库存映射*/
            WmsInventory inventoryCondition = new WmsInventory();
            inventoryCondition.setCompanyId(companyId);
            inventoryCondition.setWarehouseId(warehouseId);
            List<WmsInventory> inventoryList = wmsInventoryService.selectByEntity(inventoryCondition);
            if (Objects.isNull(inventoryList) || inventoryList.isEmpty()) {
                ErrorCode.INVENTORY_DEDUCT_INVENTORY_NOT_EXIST_40032.throwError();
            }
            Map<String, BigDecimal> groupByProductIdAndLcCode2QtyMap = inventoryList.stream().collect(Collectors.groupingBy((item -> item.getProductId() + "#" + item.getLcCode()), CollectorsUtil.summingBigDecimal(WmsInventory::getIvQty)));

            /**组装*/
            Map<String, BigDecimal> productIdAndLcCode2RealQty = Maps.newHashMap();
            groupByProductIdAndLcCodeMapForDeduct.forEach((k ,v) ->{
                String[] condtion = k.split("#");
                if(!groupByProductIdAndLcCode2QtyMap.containsKey(k)){
                    ErrorCode.INVENTORY_DEDUCT_NOT_EXIST_PRODUCT_LC_CODE_MAP_40051.throwError(condtion[0],condtion[1]);
                }
                BigDecimal dbQty = groupByProductIdAndLcCode2QtyMap.get(k);
                BigDecimal deductQty = v;

                //(产品,库位)->  比较数量
                if(dbQty.compareTo(deductQty) < 0){
                    String productName = productMap.get(Long.valueOf(condtion[0]));
                    String lcCode = condtion[1];
                    ErrorCode.INVENTORY_DEDUCT_INVENTORY_NOT_ENOUGH_40052.throwError(JSON.toJSONString(productName),JSON.toJSONString(lcCode));
                }
                productIdAndLcCode2RealQty.put(k,deductQty);
            });

            /**自定义扣减集合*/
            List<WmsInventoryDeductConditionVo> inventoryDeductConditionList = Lists.newArrayList();
            /**出库任务明细id->entity映射*/
            Map<Long, WmsDoLine> id2DoLineEntityMap = wmsDoLines.stream().collect(Collectors.toMap(WmsDoLine::getId, Function.identity()));
            Map<Long, WmsPloLine> id2PloLineEntityMap = wmsPloLineList.stream().collect(Collectors.toMap(WmsPloLine::getDolId, Function.identity()));
            /**待出库存集合(删除)*/
            List<Long> deleteIvOutList = wmsIvOutList.stream().map(WmsIvOut::getId).collect(Collectors.toList());
            List<WmsDoLine> updateDoLineList = new ArrayList<>();

            DeductEnum deductTypeEnum = DeductEnum.findDeductEnumByDeductType(containerType);
            IDeductStrategy deductStrategy = deductStrategyFactory.getDeductStrategy(deductTypeEnum.getDeductType());
            inventoryDeductConditionList = deductStrategy.process(requestProductList,requestLcCodeList,productIdAndLcCode2RealQty,wmsIvOutList, updateDoLineList,
                    id2DoLineEntityMap, id2PloLineEntityMap,dolId2LcCodeMap,dohId,warehouseId,companyId);

            //扣减库存id与扣减次数最大值集合
            List<WmsDeductIvOutConfirmResponseVo> ivIdIndexList = wmsIvOutConfirmService.selectIvIdIndex();
            log.info("\n +++++++++++++++++++++ 出库扣减库存操作::查询锁定扣减记录-扣减次数最大值 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(ivIdIndexList));
            Map<Long, Integer> ivIdIndexMap = null;
            if (Objects.nonNull(ivIdIndexList) && !ivIdIndexList.isEmpty()) {
                ivIdIndexMap = ivIdIndexList.stream().collect(Collectors.toMap(WmsDeductIvOutConfirmResponseVo::getIvId, WmsDeductIvOutConfirmResponseVo::getIvIdIndex));
            }

            //库存分组(ID+库位编码产品)
            Map<String, List<WmsInventory>> dbInventoryMap = inventoryList.stream().collect(Collectors.groupingBy((item -> item.getProductId() + "#" + item.getLcCode())));
            /**待扣减库存分组(产品ID+库位编码)统计库存数量*/
            Map<String, BigDecimal> requestCondition2QtyMap = inventoryDeductConditionList.stream().collect(Collectors.groupingBy((item -> item.getProductId() + "#" + item.getLcCode()), CollectorsUtil.summingBigDecimal(WmsInventoryDeductConditionVo::getRealQty)));
            /**待扣减库存分组(产品ID+库位编码)*/
            Map<String, List<WmsInventoryDeductConditionVo>> requestCondition2EntityMap = inventoryDeductConditionList.stream().collect(Collectors.groupingBy((item -> item.getProductId() + "#" + item.getLcCode())));
            
            //更新库存集合
            List<WmsInventory> updateInventoryList = Lists.newArrayList();
            //删除库存集合(id)
            List<Long> deleteInventoryList = Lists.newArrayList();
            //库存变动集合
            List<WmsIvTransaction> addIvTransactionList = Lists.newArrayList();
            //库存锁定扣减集合
            List<WmsIvOutConfirm> addIvOutConfirmList = Lists.newArrayList();


            /**基本信息*/
            /**当前UTC时间*/
            Date nowWithUTC = DateUtils.nowWithUTC();
            /**登录用户信息*/
            UserInfo userInfo = CustomRequestContext.getUserInfo();
            /**库存扣减次数映射 */
            Map<Long, Integer> finalIvIdIndexMap = ivIdIndexMap;

            /**遍历 待扣减库存分组(产品ID+库位编码)统计库存数量 映射*/
            requestCondition2QtyMap.forEach((k, v) -> {
                if (!dbInventoryMap.containsKey(k)) {
                    ErrorCode.INVENTORY_DEDUCT_PRODUCT_NOT_EXIST_40033.throwError(k);
                }
                List<WmsInventory> list = dbInventoryMap.get(k);
                if (Objects.isNull(list) || dbInventoryMap.isEmpty()) {
                    ErrorCode.INVENTORY_DEDUCT_PRODUCT_NOT_EXIST_40034.throwError(k);
                }

                //商品库存按照批次id升序排列(db)
                List<WmsInventory> sortedList = list.stream().sorted(Comparator.comparing(WmsInventory::getIabId)).collect(toList());
                BigDecimal requestIvQty = v;//请求扣减库存数
                for (WmsInventory inventory : sortedList) {
                    BigDecimal dbIvQty = inventory.getIvQty();//库存记录中的库存商品库存数量
                    int compareRet = dbIvQty.compareTo(requestIvQty);//当前库存记录中的商品库存数量与请求扣减库存数量进行对比
                    //当前库存记录的商品库存数量可以 > 请求扣减的库存数
                    if (compareRet > 0) {
                        BigDecimal curIvqty = dbIvQty.subtract(requestIvQty);
                        inventory.setIvQty(curIvqty);//设置当前库存数量(扣减之后的库存数量)
                        inventory.setIvTranstime(nowWithUTC);//最近库存更新时间
                        updateInventoryList.add(inventory);//添加到库存更新集合中
                        createTransactionRecord(wmsDoHeader, requestCondition2EntityMap, addIvTransactionList, nowWithUTC, userInfo, k, requestIvQty, inventory, dbIvQty, curIvqty);//新增库存变更记录
                        createIvOutConfirmRecord(addIvOutConfirmList, wmsDoHeader, requestCondition2EntityMap, nowWithUTC, finalIvIdIndexMap, k, requestIvQty, inventory);//新增库存锁定记录
                        break;
                    }
                      /**删除库存记录*/
                      deleteInventoryList.add(inventory.getId());
                      /**创建库存变更记录*/
                      createTransactionRecord(wmsDoHeader, requestCondition2EntityMap, addIvTransactionList, nowWithUTC, userInfo, k, requestIvQty, inventory, dbIvQty, BigDecimal.ZERO);
                      /**创建库存扣减记录*/
                      createIvOutConfirmRecord(addIvOutConfirmList, wmsDoHeader, requestCondition2EntityMap, nowWithUTC, finalIvIdIndexMap, k, requestIvQty, inventory);

                    //当前库存记录商品数量 == 请求扣减
                    if (compareRet == 0) {
                        break;
                    }
                    //当前库存记录商品数量  < 请求扣减
                    requestIvQty = requestIvQty.subtract(dbIvQty);
                }
            });

            /**回写出库任务单头*/
            wmsDoHeader.setOrderStatus(ALL_OUT_INVENTORY_STATUS);//已出库
            wmsDoHeader.setShippingTime(nowWithUTC);//发货时间(出库时间)
            wmsDoHeader.setShipQty(shipQty);//实际出库数量
            BigDecimal shipVol= updateDoLineList.stream().map(WmsDoLine::getShipVol).reduce(BigDecimal.ZERO, BigDecimal::add);
            wmsDoHeader.setShipVol(shipVol);//出库体积
            BigDecimal shipWeight= updateDoLineList.stream().map(WmsDoLine::getShipWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
            wmsDoHeader.setShipWeight(shipWeight);
            wmsDoHeader.setShipDetailedSpeciesQty(updateDoLineList.size());//出库货品种类数

            InventoryDeductBussinessResponse response = new InventoryDeductBussinessResponse();
            if (LockMapUtil.confirmLock(dohId, lockValue)) {
                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 删除待出库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(deleteIvOutList));
                int countForIvOut = wmsIvOutService.deleteByPrimaryKeys(deleteIvOutList);
                if (countForIvOut <= 0) {
                    ErrorCode.INVENTORY_DEDUCT_DELETE_IV_OUT_40035.throwError();
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 新增库存锁定扣减记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(addIvOutConfirmList));
                int countForOutConfirm = wmsIvOutConfirmService.insertBatch(addIvOutConfirmList);
                if (countForOutConfirm <= 0) {
                    ErrorCode.INVENTORY_DEDUCT_ADD_OUT_CONFIRM_40036.throwError();
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 新增库存变动记录-> {} ++++++++++++++++++++ \n ", JSON.toJSONString(addIvTransactionList));
                int countForTransaction = wmsIvTransactionService.insertBatch(addIvTransactionList);
                if (countForTransaction <= 0) {
                    ErrorCode.INVENTORY_DEDUCT_ADD_TRANSACTION_40037.throwError();
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 更新库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(updateInventoryList));
                if (Objects.nonNull(updateInventoryList) && !updateInventoryList.isEmpty()) {
                    int countForInventoryUpdate = wmsInventoryService.updateBatch(updateInventoryList);
                    if (countForInventoryUpdate <= 0) {
                        ErrorCode.INVENTORY_DEDUCT_UPDATE_INVENTORY_40038.throwError();
                    }
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 删除库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(deleteInventoryList));
                if (Objects.nonNull(deleteInventoryList) && !deleteInventoryList.isEmpty()) {
                    int countForInventoryDelete = wmsInventoryService.deleteByPrimaryKeys(deleteInventoryList);
                    if (countForInventoryDelete <= 0) {
                        ErrorCode.INVENTORY_DEDUCT_DELETE_INVENTORY_40039.throwError();
                    }
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 更新出库单单头 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsDoHeader));
                int countForDoheader = wmsDoHeaderService.updateByPrimaryKeySelective(wmsDoHeader);
                if (countForDoheader <= 0) {
                    ErrorCode.INVENTORY_DEDUCT_UPDATE_DO_HEADER_40040.throwError();
                }

                log.info("\n +++++++++++++++++++++ 出库扣减库存操作:: 更新出库明细 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(updateDoLineList));
                int countForDoLine = wmsDoLineService.updateBatch(updateDoLineList);
                if(countForDoLine <= 0){
                    ErrorCode.INVENTORY_DEDUCT_UPDATE_DO_LINE_40053.throwError();
                }
                /**异步回写OMS(出库计划单头/明细 销售单单头/明细 库存变动的相关记录)  inventoryDeductConditionList*/
                List<OmsIvOutWriteBack.OmsIvOutWriteBackCondition> omsIvOutWriteBacks = DataConvertUtil.parseDataAsArray(inventoryDeductConditionList, OmsIvOutWriteBack.OmsIvOutWriteBackCondition.class);
                OmsIvOutWriteBack omsIvOutWriteBack = new OmsIvOutWriteBack();
                omsIvOutWriteBack.setList(omsIvOutWriteBacks);
                omsIvOutWriteBack.setOrderType(orderType);
                WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsIvOutWriteBackProducer.getQueueName(), "", omsIvOutWriteBack);
                wmsOmsIvOutWriteBackProducer.send(wmsIdInstantdirective);

                if ("03".equals(wmsDoHeader.getOrderType())) {
                    //内部供应商自动创建的单据类型
//                String poId = wmsDoHeaderService.findPoId(wmsDoHeader.getId());
//                if (StringUtils.isEmpty(poId)) {
//                    //未找到关联的单据id
//                    ErrorCode.DO_ERROR_4002.throwError();
//                }
                    //获取明细信息
                    List<RecovicePlanAddByDoRequest.Detail> requestDetails = new ArrayList<>();
                    wmsDoLines.stream().forEach(item -> {
                        RecovicePlanAddByDoRequest.Detail requestDetail = new RecovicePlanAddByDoRequest.Detail();
                        requestDetail.setProductId(item.getProductId());
                        requestDetail.setPlanQty(item.getQty());
                        requestDetails.add(requestDetail);
                    });
                    if (requestDetails.isEmpty()) {
                        //出库产品不能为空
                        ErrorCode.DO_ERROR_4003.throwError();
                    }
                    RecovicePlanAddByDoRequest recovicePlanAddByDoRequest = new RecovicePlanAddByDoRequest();
                    recovicePlanAddByDoRequest.setPoId(wmsDoHeader.getPoId());
                    recovicePlanAddByDoRequest.setDetails(requestDetails);
                    log.debug("******recovicePlanAddByDoRequest******" + JSON.toJSONString(recovicePlanAddByDoRequest));
                    //添加数据交互指令
                    WmsIdInstantdirective instantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsRecovicePlanAddByDoProducer.getQueueName(), "", recovicePlanAddByDoRequest);
                    //发送消息到队列
                    wmsOmsRecovicePlanAddByDoProducer.send(instantdirective);
                }
            }else{
                response.setIsSuccess(false);
            }
            return response;
        } finally {
            LockMapUtil.cancelLock(dohId, lockValue);
        }
    }

    /***
     * 创建库存锁定扣减记录
     *
     * @param wmsDoHeader
     * @param requestCondition2EntityMap
     * @param nowWithUTC
     * @param finalIvIdIndexMap
     * @param k
     * @param requestIvQty
     * @param inventory
     */
    private void createIvOutConfirmRecord(List<WmsIvOutConfirm> addIvOutConfirmList, WmsDoHeader wmsDoHeader, Map<String, List<WmsInventoryDeductConditionVo>> requestCondition2EntityMap, Date nowWithUTC, Map<Long, Integer> finalIvIdIndexMap, String k, BigDecimal requestIvQty, WmsInventory inventory) {
        WmsIvOutConfirm wmsIvOutConfirm = new WmsIvOutConfirm();
        wmsIvOutConfirm.setId(idGenerator.getUnique());
        wmsIvOutConfirm.setIvoId(requestCondition2EntityMap.get(k).get(0).getIvoId());//待出库存id
        wmsIvOutConfirm.setIvId(inventory.getIvId());//库存id
        wmsIvOutConfirm.setIvIdIndex(Objects.isNull(finalIvIdIndexMap) || Objects.isNull(finalIvIdIndexMap.get(inventory.getIvId()))? 1 : finalIvIdIndexMap.get(inventory.getIvId()) + 1);//扣减次数
        wmsIvOutConfirm.setIvocQty(requestIvQty);//扣减锁定库存数量
        wmsIvOutConfirm.setApCodeDc("DOMT");//应用类型代码
        wmsIvOutConfirm.setIvDocumentCode(wmsDoHeader.getDohCode());//引起库存变动业务单据编号
        wmsIvOutConfirm.setIvDocumentId(wmsDoHeader.getId());//引起库存变动的单据id
        wmsIvOutConfirm.setIvDocumentlineId(requestCondition2EntityMap.get(k).get(0).getLineId());//引起库存变动单据明细id
        wmsIvOutConfirm.setLcCode(requestCondition2EntityMap.get(k).get(0).getLcCode());//库位
        wmsIvOutConfirm.setConfirmTime(nowWithUTC);//确认时间
        addIvOutConfirmList.add(wmsIvOutConfirm);
    }

    /**
     * 新增库存变动记录
     *
     * @param wmsDoHeader
     * @param requestCondition2EntityMap
     * @param addIvTransactionList
     * @param nowWithUTC
     * @param userInfo
     * @param k
     * @param requestIvQty
     * @param inventory
     * @param dbIvQty
     * @param afterQty
     */
    private void createTransactionRecord(WmsDoHeader wmsDoHeader, Map<String, List<WmsInventoryDeductConditionVo>> requestCondition2EntityMap, List<WmsIvTransaction> addIvTransactionList, Date nowWithUTC, UserInfo userInfo, String k, BigDecimal requestIvQty, WmsInventory inventory, BigDecimal dbIvQty, BigDecimal afterQty) {
        WmsIvTransaction wmsIvTransaction = DataConvertUtil.parseDataAsObject(inventory, WmsIvTransaction.class);
        wmsIvTransaction.setId(idGenerator.getUnique());
        wmsIvTransaction.setApCode("SHIP");/**应用类型-签出扣减*/
        wmsIvTransaction.setIvtDocumentCode(wmsDoHeader.getDohCode());/**引起库存变动业务单据编码*/
        wmsIvTransaction.setIvtDocumentId(wmsDoHeader.getId());/**引起库存变动的单据id*/
        wmsIvTransaction.setIvtDocumentlineId(requestCondition2EntityMap.get(k).get(0).getLineId());/**引起库存变动的单据明细id*/
        wmsIvTransaction.setIvtChangeType("LESS");/**库存变动类型*/
        wmsIvTransaction.setLcCodeFrom(inventory.getLcCode());/**原库位编码*/
        wmsIvTransaction.setLcCodeTo(inventory.getLcCode());/**目标库位编码*/
        wmsIvTransaction.setIvQtyFrom(dbIvQty);/**原数量*/
        wmsIvTransaction.setIvQtyTo(afterQty);/**调整后数量*/
        wmsIvTransaction.setDcQty(requestIvQty);/**调整数量*/
        wmsIvTransaction.setCreateTime(nowWithUTC);
        wmsIvTransaction.setCreateUser(userInfo.getUserName());
        addIvTransactionList.add(wmsIvTransaction);
    }

    /**
     * 库存变动分页记录(核验)
     *
     * @param request
     * @return
     */
    public InventoryChangePageQueryBussinessResponse changePageQueryInventory(InventoryChangePageQueryBussinessRequest request) {
        Pagination pagination = new Pagination(request.getPageCommonRequest().getPageNumber(), request.getPageCommonRequest().getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        InventoryChangePageQueryBussinessRequest.InventoryChangePageQueryCondition queryCondition = request.getInventoryChangePageQueryCondition();
        if(Objects.nonNull(queryCondition)){
            String startTime = queryCondition.getStartTime();
            String endTime = queryCondition.getEndTime();
            if(Objects.nonNull(startTime) && StringUtils.isNoneBlank(startTime) &&
                Objects.nonNull(endTime) && StringUtils.isNoneBlank(endTime)
            )

            {
                startTime = startTime.split(" ")[0]+ RANGE_TIME_START;
                endTime = endTime.split(" ")[0] + RANGE_TIME_END;
                conditionsBuilder.between("createTime",startTime,endTime);
            }

            if(Objects.nonNull(startTime) && StringUtils.isNoneBlank(startTime)){
                 startTime = startTime.split(" ")[0]+ RANGE_TIME_START;
                 conditionsBuilder.ge("createTime",startTime);
            }

            if(Objects.nonNull(endTime) && StringUtils.isNoneBlank(endTime)){
                 endTime = endTime.split(" ")[0] + RANGE_TIME_END;
                 conditionsBuilder.le("createTime",endTime);
            }
        }
        conditionsBuilder.orderBy("create_time desc");
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination)wmsIvTransactionDailyKnotService.selectCustomPage(pagination, paramMap);
        InventoryChangePageQueryBussinessResponse response = new InventoryChangePageQueryBussinessResponse();
        response.setPagination(page);
        return response;
    }

    /**
     * 库存变动列表(核验)
     * @param request
     * @return
     */
    public InventoryDeductBussinessResponse changeListInventory(InventoryChangeListBussinessRequest request) {
        String curDate = DateUtils.formatUseYmd(request.getCreateTime());
        String startCurdate= curDate + RANGE_TIME_START;
        String endCurdate= curDate + RANGE_TIME_END;
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        conditionsBuilder.between("createTime",startCurdate,endCurdate);
        Map<String, Object> paramMap = conditionsBuilder.build();
        List<WmsIvTransactionDailyKnot> list = wmsIvTransactionDailyKnotService.selectByCustomCondtions(paramMap);
        InventoryDeductBussinessResponse response = new InventoryDeductBussinessResponse();
        response.setList(list);
        return response;
    }

    /**
     * 多样品库位检查
     *
     * @param request
     * @return
     */
    public InventoryMultiSampleLocationCheckBussinessResponse multiSampleLocationCheckInventory(InventoryMultiSampleLocationCheckBussinessRequest request) {
        List<InventoryMultiSampleLocationCheckBussinessRequest.InventoryMultiSampleLocationCheckCondition> requestList = request.getList();
        if(Objects.isNull(requestList) || requestList.isEmpty() ){
            ErrorCode.INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40045.throwError();
        }
        List<Long> requestProductList = requestList.stream().map(InventoryMultiSampleLocationCheckBussinessRequest.InventoryMultiSampleLocationCheckCondition::getProductId).collect(toList());
        if(Objects.isNull(requestProductList) || requestProductList.isEmpty()){
            ErrorCode.INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40046.throwError();
        }
        ProductRequest productDetailRequest = new ProductRequest();
        productDetailRequest.setProductIds(requestProductList);
        productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        ApiResponse productListResponse = productFeignClient.getProductList(productDetailRequest);
        ProductListResponse productResponse = JSON.parseObject(JSON.toJSONString(productListResponse.getData()), ProductListResponse.class);
        List<ProductDetailResponse> productList = productResponse.getProductList();
        if (Objects.isNull(productList) || productList.isEmpty()) {
            ErrorCode.INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40047.throwError(JSON.toJSONString(requestProductList));
        }
        Map<Long, String> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, ProductDetailResponse::getProductName));
        Map<String, List<InventoryMultiSampleLocationCheckBussinessRequest.InventoryMultiSampleLocationCheckCondition>> requestGroupMap = requestList.stream().collect(Collectors.groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId())));
        List<WmsInventory> dbList = wmsInventoryService.selectAll();
        Map<String, String> lcCodeMap = Maps.newHashMap();
        if(Objects.nonNull(dbList) && !dbList.isEmpty()){
            Map<String, List<InventoryMultiSampleLocationCheckBussinessRequest.InventoryMultiSampleLocationCheckCondition>> frontGroupMap = requestList.stream().collect(Collectors.groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId())));
            Map<String, List<WmsInventory>> dbGroupMap = dbList.stream().collect(Collectors.groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId())));
            frontGroupMap.forEach((k,v)->{
                if(dbGroupMap.containsKey(k)){
                    Set<String> lcCodeSet = Sets.newConcurrentHashSet();
                    Long productId = Long.valueOf(k.split("#")[2]);
                    String productName = productMap.get(productId);
                    List<WmsInventory> inventories = dbGroupMap.get(k);
                    inventories.forEach(item ->{
                        String lcCode = item.getLcCode();
                        if(StringUtils.isNoneBlank(lcCode) && lcCode.toLowerCase().startsWith("s")){
                            lcCodeSet.add(lcCode);
                        }
                    });
                    if(lcCodeSet.size() > 1){
                        lcCodeMap.put(productName,JSON.toJSONString(lcCodeSet));
                    }
                }
            });
        }
        InventoryMultiSampleLocationCheckBussinessResponse response = new InventoryMultiSampleLocationCheckBussinessResponse();
        response.setLcCodeMap(lcCodeMap);
        return response;
    }

}
