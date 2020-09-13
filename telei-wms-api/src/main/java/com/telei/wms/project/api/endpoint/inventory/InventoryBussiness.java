package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.nuochen.framework.component.commons.ExceptionHelper;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.commons.utils.CollectorsUtil;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.inventoryAddWriteBack.OmsInventoryAddWriteBack;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.customer.product.dto.ProductRequest;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryAddWriteBackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryChangeWriteBackProducer;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
import com.telei.wms.project.api.endpoint.inventory.strategy.AdjustStrategyFactory;
import com.telei.wms.project.api.endpoint.inventory.strategy.IAdjustStrategy;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: leo
 * @date: 2020/8/26 09:35
 */
@Slf4j
@Service
public class InventoryBussiness {
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


    /**
     * 入库(上架)
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryAddBussinessResponse addInventory(InventoryAddBussinessRequest request) {
        List<InventoryAddBussinessRequest.InventoryAddRequestCondition> requestList = request.getList();
        if (requestList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        //1.基本信息提取
        //1.1 基本信息提取-上架单单头id集合
        List<Long> requestPaoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaoId).collect(Collectors.toList());
        if (Objects.isNull(requestPaoIdList) || requestPaoIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020.throwError();
        }
        //1.2 基本信息提取-上架单明细id集合
        List<Long> requestPaolIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId).collect(Collectors.toList());
        if (Objects.isNull(requestPaolIdList) || requestPaolIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006.throwError();
        }
        //1.3 基本信息提取-库存批次id集合
        List<Long> requestIabIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIabId).collect(Collectors.toList());
        if (Objects.isNull(requestIabIdList) || requestIabIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004.throwError();
        }
        //1.4 基本信息提取-收货单单头id集合
        List<Long> requestRooIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId).collect(Collectors.toList());
        if (Objects.isNull(requestRooIdList) || requestRooIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }
        //1.5 基本信息提取-收货单明细集合
        List<Long> requestRooLIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoolId).collect(Collectors.toList());
        if (Objects.isNull(requestRooLIdList) || requestRooLIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021.throwError();
        }
        //1.6 基本信息提取-入库任务单头id集合
        List<Long> requestRoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId).collect(Collectors.toList());
        if (Objects.isNull(requestRoIdList) || requestRoIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010.throwError();
        }
        //1.8 基本信息提取-产品id集合
        List<Long> requestProductIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getProductId).collect(Collectors.toList());
        if (Objects.isNull(requestProductIdList) || requestProductIdList.isEmpty()) {
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002.throwError();
        }

        //获取锁-相关单据单头作为参数(上架单单头、库存批次单头、收货单单头、入库单头);
        List<Long> lockKey = Stream.of(requestPaoIdList, requestIabIdList, requestRoIdList, requestRoIdList).flatMap(Collection::stream).collect(Collectors.toList());
        Object lockValue = LockMapUtil.tryLock(lockKey);
        try {
            Map<String, BigDecimal> requestInventoryMap = requestList.stream().collect(Collectors.groupingBy(item -> item.getLcCode() + "#" + item.getProductId(), CollectorsUtil.summingBigDecimal(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty)));
//            List<WmsInventory> inventoryList = DataConvertUtil.parseDataAsArray(requestList, WmsInventory.class);
            //2公用信息提取
            //2.1 UTC时间
            Date nowWithUTC = DateUtils.nowWithUTC();
            //2.2 用户信息(请求上下文)
            UserInfo userInfo = CustomRequestContext.getUserInfo();

            //3、预处理
            //3.1 产品(商品)-预处理
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setIds(requestProductIdList);
            productDetailRequest.setCompanyId(userInfo.getCompanyId());
            ApiResponse productListResponse = productFeignClient.selectProductList(productDetailRequest);
            ProductListResponse response = JSON.parseObject(JSON.toJSONString(productListResponse.getData()), ProductListResponse.class);
            List<ProductDetailResponse> productList = response.getProductList();
            if (Objects.isNull(productList) || productList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003.throwError(JSON.toJSONString(requestProductIdList));
            }
            Map<Long, ProductDetailResponse> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, Function.identity()));

            //2.2 库存批次-预处理
            List<WmsIvAttributebatch> ivAttributebatches = wmsIvAttributebatchService.selectByPrimaryKeys(requestIabIdList);
            if (Objects.isNull(ivAttributebatches) || ivAttributebatches.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005.throwError(JSON.toJSONString(requestIabIdList));
            }
            Map<Long, WmsIvAttributebatch> ivAttributeBatchMap = ivAttributebatches.stream().collect(Collectors.toMap(WmsIvAttributebatch::getId, Function.identity()));

            //2.3 上架单单头-预处理(上架单单头 上架时间、上架用户、已上架数量)
            Long paoId = requestList.get(0).getPaoId();
            WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(paoId);
            if(Objects.isNull(wmsPaoHeader)){
                ErrorCode.INVENTORY_ADD_ERROR_PAO_NOT_EXIST_40025.throwError(paoId);
            }
            wmsPaoHeader.setPutawayUser(userInfo.getUserName());
            wmsPaoHeader.setPutawayTime(nowWithUTC);
            BigDecimal waitPutawayQty = requestList.stream().collect(CollectorsUtil.summingBigDecimal(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
            wmsPaoHeader.setPutawayQty(Objects.isNull(wmsPaoHeader.getPutawayQty()) ? waitPutawayQty : (wmsPaoHeader.getPutawayQty().add(waitPutawayQty)));
            wmsPaoHeader.setLastupdateUser(userInfo.getUserName());
            wmsPaoHeader.setLastupdateTime(nowWithUTC);

            //2.4 上架单明细-预处理(上架时间、上架用户、上架数量)
            List<WmsPaoLine> paoLineList = wmsPaoLineService.selectByPrimaryKeys(requestPaolIdList);
            if(Objects.isNull(paoLineList) || paoLineList.isEmpty()){
                ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError(JSON.toJSONString(requestPaolIdList));
            }
            Map<Long, InventoryAddBussinessRequest.InventoryAddRequestCondition> paolId2EntityMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId, Function.identity()));
            paoLineList.stream().forEach(item -> {
                item.setPaolQty(paolId2EntityMap.get(item.getId()).getIvQty());
                item.setPutawayTime(nowWithUTC);
                item.setPutawayUser(userInfo.getUserName());
            });

            //2.5 收货单单头-预处理(更新 putawayQty)
            Map<Long, BigDecimal> rooQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
            if (Objects.isNull(rooQtyMap) || rooQtyMap.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
            }

            List<WmsRooHeader> rooHeaderList = wmsRooHeaderService.selectByPrimaryKeys(requestRooIdList);
            if (Objects.isNull(rooHeaderList) || rooHeaderList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008.throwError(JSON.toJSONString(requestRooIdList));
            }
            rooHeaderList.forEach(item -> {
                item.setPutawayQty(Objects.isNull(item.getPutawayQty()) ? rooQtyMap.get(item.getId()): item.getPutawayQty().add(rooQtyMap.get(item.getId())));
            });

            //2.6 入库任务单头-预处理(上架数量累加:putawayQty 上架完成时间:putawayAllTime(when=putawayQty>=totalQty)  总计划入库数量:totalQty)
            Map<Long, BigDecimal> roQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId,
                    InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));

            List<WmsRoHeader> roHeaderList = wmsRoHeaderService.selectByPrimaryKeys(requestRoIdList);
            if (Objects.isNull(roHeaderList) || roHeaderList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError(JSON.toJSONString(requestRoIdList));
            }
            roHeaderList.stream().forEach(item -> {
                item.setPutawayQty(Objects.isNull(item.getPutawayQty())?roQtyMap.get(item.getId()):item.getPutawayQty().add(roQtyMap.get(item.getId())));
                if (item.getTotalQty().compareTo(item.getPutawayQty()) >= 0) {
                    item.setPutawayAllTime(nowWithUTC);
                    item.setOrderStatus("50");
                    return;
                }
                item.setOrderStatus("40");
            });

            //2.7 oms-入库计划单(单头/明细)-预处理 oms-采购单(单头/明细)-预处理
            List<Long> rolIdList = paoLineList.stream().map(WmsPaoLine::getRolId).collect(Collectors.toList());
            if (Objects.isNull(rolIdList) || rolIdList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012.throwError();
            }
            List<WmsRoLine> roLineList = wmsRoLineService.selectByPrimaryKeys(rolIdList);
            if (Objects.isNull(roLineList) || roLineList.isEmpty()) {
                ErrorCode.INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013.throwError(JSON.toJSONString(rolIdList));
            }
            Map<Long, WmsPaoLine> paoLineRolIdToEntityMap = paoLineList.stream().collect(Collectors.toMap(WmsPaoLine::getRolId, Function.identity()));
            List<OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition> omsInventoryAddWriteBackConditionList = new ArrayList<>();
            roLineList.stream().forEach(item -> {
                OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition omsInventoryAddWriteBackCondition = new OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition();
                omsInventoryAddWriteBackCondition.setRpId(item.getRpId());//入库计划id
                omsInventoryAddWriteBackCondition.setRpdId(item.getRpdId());//入库计划明细id
                omsInventoryAddWriteBackCondition.setPoId(item.getPoId());//采购id
                omsInventoryAddWriteBackCondition.setPodId(item.getPodId());//采购明细id
                omsInventoryAddWriteBackCondition.setIvQty(paoLineRolIdToEntityMap.get(item.getId()).getPaolQty());
                omsInventoryAddWriteBackConditionList.add(omsInventoryAddWriteBackCondition);
            });
            OmsInventoryAddWriteBack omsInventoryAddWriteBack = new OmsInventoryAddWriteBack();
            omsInventoryAddWriteBack.setList(omsInventoryAddWriteBackConditionList);

            //2.8 库存记录-预处理
//            Map<String, InventoryAddBussinessRequest.InventoryAddRequestCondition> reqeustLcCodeWithProductId2EntityMap = requestList.stream().collect(Collectors.toMap(item -> item.getLcCode() + "#" + item.getProductId(), Function.identity()));
            Map<String, List<InventoryAddBussinessRequest.InventoryAddRequestCondition>> reqeustLcCodeWithProductId2EntityMap = requestList.stream().collect(Collectors.groupingBy(item -> item.getLcCode() + "#" + item.getProductId()));

            List<WmsInventory> inventoryList = Lists.newArrayList();
            requestInventoryMap.forEach((k,v) ->{
                List<InventoryAddBussinessRequest.InventoryAddRequestCondition> requestConditionList = reqeustLcCodeWithProductId2EntityMap.get(k);
                InventoryAddBussinessRequest.InventoryAddRequestCondition requestCondition = requestConditionList.get(0);
                WmsInventory inventory = new WmsInventory();
                String[] arr = k.split("#");
                String lcCode = arr[0];
                Long productId=Long.valueOf(arr[1]);
                ProductDetailResponse productDetail = productMap.get(productId);//产品记录
                WmsIvAttributebatch ivAttributebatchDetail = ivAttributeBatchMap.get(requestCondition.getIabId());//批次记录

                BigDecimal bigBagRate = new BigDecimal(ivAttributebatchDetail.getBigBagRate());//大包转转换率(批次表记录)
                BigDecimal midBagRate = new BigDecimal(ivAttributebatchDetail.getMidBagRate());//中包转化数
                BigDecimal ivQty = v;//库存数量


                inventory.setId(idGenerator.getUnique());//主键id
                inventory.setCompanyId(requestCondition.getCompanyId());//公司id
                inventory.setWarehouseId(requestCondition.getWarehouseId());//仓库id
                inventory.setWarehouseCode(requestCondition.getWarehouseCode());//仓库编码
                inventory.setLcCode(lcCode);//库位
                inventory.setProductId(productId);//产品id
                inventory.setProductDate(ivAttributebatchDetail.getProductDate());//生产日期
                inventory.setIvQty(ivQty);//库存数量
                inventory.setIabId(ivAttributebatchDetail.getId());//批次id
                inventory.setIvDocumentlineId(requestCondition.getPaolId());//引起库存变动的单据明细id
                inventory.setIvDocumentId(requestCondition.getPaoId());//引起库存变动的单据id
                inventory.setIvDocumentCode(wmsPaoHeader.getPaoCode());//引起库存变动的单据编码
                inventory.setApCodeDc("RECV");//引起本次变动库存变动的来源单据引用类型
                inventory.setQsCode("GD");//实物类别

                inventory.setIvFifoTime(ivAttributebatchDetail.getCreateTime());//先进先出时间(获取跑批次表的创建时间)
                inventory.setStockUnit(productDetail.getStockUnit());;//计量单位
                inventory.setBigBagRate(bigBagRate);//大包转换率  批次表中获取
                inventory.setBigBagQty(ivQty.divideAndRemainder(bigBagRate)[0]);//大包数量
                inventory.setBigBagExtraQty(ivQty.divideAndRemainder(bigBagRate)[1]);//大包剩余数量
                inventory.setMidBagRate(midBagRate); // 中包转化数(批次表记录)
                inventory.setMidBagQty(ivQty.divideAndRemainder(midBagRate)[0]);//中包数量
                inventory.setMidBagExtraQty(ivQty.divideAndRemainder(midBagRate)[1]);//中包剩余数量
                inventory.setIvTranstime(nowWithUTC);//最近库存更新时间
                inventory.setIvCreatetime(nowWithUTC);//创建时间
                inventory.setBizDate(nowWithUTC);//业务日期(与创建时间保持一致)
                inventoryList.add(inventory);
            });

            //3、处理(状态数据变更)
            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                inventoryDataProcess(inventoryList, wmsPaoHeader, paoLineList, rooHeaderList, roHeaderList, omsInventoryAddWriteBack,userInfo,nowWithUTC);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            LockMapUtil.cancelLock(lockKey, lockValue);
        }

        return new InventoryAddBussinessResponse();
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
                                      List<WmsRoHeader> roHeaderList, OmsInventoryAddWriteBack omsInventoryAddWriteBack,UserInfo userInfo,Date nowWithUTC) {
        //3.1 库存/库存历史处理  插入wms_inventory wms_inventory_history wms_iv_transaction
        int countForInventory = wmsInventoryService.insertBatch(inventoryList);
        if (countForInventory != inventoryList.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4014.throwError();
        }
        List<WmsInventoryHistory> inventoryHistoryList = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventoryHistory.class);

        int countForInventoryHistory = wmsInventoryHistoryService.insertBatch(inventoryHistoryList);
        if (countForInventoryHistory != inventoryHistoryList.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4015.throwError();
        }
        Map<Long, WmsInventory> ivId2EntityMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getId, Function.identity()));
        List<WmsIvTransaction> wmsIvTransactions = DataConvertUtil.parseDataAsArray(inventoryList, WmsIvTransaction.class);
        wmsIvTransactions.stream().forEach(item ->{
            WmsInventory inventory = ivId2EntityMap.get(item.getIvId());
            item.setCreateUser(userInfo.getUserName());//创建用户
            item.setCreateTime(nowWithUTC);//创建时间
            item.setApCode(inventory.getApCodeDc());//应用类型编码
            item.setIvtChangeType("INCR");//变动类型
            item.setIvtDocumentCode(inventory.getIvDocumentCode());//引起库存变动业务单据号
            item.setIvtDocumentId(inventory.getIvDocumentId());//引起库存变动业务单据id
            item.setIvtDocumentlineId(inventory.getIvDocumentlineId());//引起库存变动业务单据明细id
            item.setDcQty(inventory.getIvQty());//调整数量
        });
        int countForTransaction = wmsIvTransactionService.insertBatch(wmsIvTransactions);
        if (countForTransaction != wmsIvTransactions.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4023.throwError();
        }

        //3.2 更新上架单单头/上架单明细
        int countForPao = wmsPaoHeaderService.updateByPrimaryKey(wmsPaoHeader);
        if (countForPao <= 0) {
            ErrorCode.INVENTORY_ADD_ERROR_4016.throwError();
        }
        int countForPaoLine = wmsPaoLineService.updateBatch(paoLineList);
        if (countForPaoLine != paoLineList.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4017.throwError();
        }
        //2.2 更新收货单单头
        int countForRootHeader = wmsRooHeaderService.updateBatch(rooHeaderList);
        if (countForRootHeader != rooHeaderList.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4018.throwError();
        }
        //2.3 更新入库任务单头
        int countForRoHeader = wmsRoHeaderService.updateBatch(roHeaderList);
        if (countForRoHeader != roHeaderList.size()) {
            ErrorCode.INVENTORY_ADD_ERROR_4019.throwError();
        }

        //4、异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)
        //4.1 指令入库
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsInventoryAddWriteBack);
        //4.2 MQ发送指令
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
        adjustInventory(request, "INCREASE");
        return new InventoryIncreaseBussinessResponse();
    }


    /***
     * 库存-调少
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryReduceBussinessResponse reduceInventory(InventoryReduceBussinessRequest request) {
        adjustInventory(request, "REDUCE");
        return new InventoryReduceBussinessResponse();
    }


    /**
     * 移库
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryShiftBussinessResponse shiftInventory(InventoryShiftBussinessRequest request) {
        adjustInventory(request, "MOVE");
        return new InventoryShiftBussinessResponse();
    }

    /**
     * 库存调整
     *
     * @param request
     * @param adjhType
     */
    private void adjustInventory(Object request, String adjhType) {
        /**基础信息*/
        Date nowWithUTC = DateUtils.nowWithUTC();
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        long adjtId = idGenerator.getUnique();//库存调整单单头id
        String bussinessNumber = getBussinessNumber("wms");

        /**库存调整单单头*/
        WmsAdjtHeader wmsAdjtHeader = DataConvertUtil.parseDataAsObject(request, WmsAdjtHeader.class);
        ProductRequest productRequest = new ProductRequest();
        Long productId = Long.valueOf(String.valueOf(((Map) JSON.toJSON(request)).get("productId")));
        productRequest.setProductIds(Arrays.asList(productId));
        ApiResponse apiResponse = productFeignClient.getProductList(productRequest);
        List<ProductDetailResponse> productDetailResponseList = DataConvertUtil.parseDataAsArray(apiResponse.getData(), ProductDetailResponse.class);
        if (Objects.isNull(productDetailResponseList)  || productDetailResponseList.isEmpty()) {
            ErrorCode.ADJT_ERROR_4001.throwError(wmsAdjtHeader.getProductBarcode(), adjhType);
        }
        wmsAdjtHeader.setProductNameLocal(productDetailResponseList.get(0).getProductNameLocal());//产品本地名称
        wmsAdjtHeader.setProductUpcCode(productDetailResponseList.get(0).getProductUpcCode());//UPC码
        wmsAdjtHeader.setAdjhCode(bussinessNumber);//业务单据编码
        wmsAdjtHeader.setId(adjtId);
        wmsAdjtHeader.setBizDate(nowWithUTC);//业务日期
        wmsAdjtHeader.setCreateTime(nowWithUTC);//创建时间
        wmsAdjtHeader.setAuditTime(nowWithUTC);//审核时间
        wmsAdjtHeader.setCreateUser(userInfo.getUserName());//创建用户
        wmsAdjtHeader.setAuditUser(userInfo.getUserName());// 审核用户
        wmsAdjtHeader.setAdjhType(adjhType);//调整类型
        wmsAdjtHeader.setIvihStatus("20");//库存调整状态

        /**库存调整单明细*/
        WmsInventory wmsInventory = new WmsInventory();
        wmsInventory.setLcCode(wmsAdjtHeader.getLcCode());
        wmsInventory.setProductId(wmsAdjtHeader.getProductId());
        //库存明细
        List<WmsInventory> wmsInventories = wmsInventoryService.selectByLcCodeAndProductId(wmsInventory);
        if (Objects.isNull(wmsInventories) || wmsInventories.isEmpty()) {
            ErrorCode.ADJT_ERROR_4002.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
        }
        Object lockKey = adjtId;
        Object lockValue = LockMapUtil.tryLock(lockKey);
        try {

            List<WmsAdjtLine> adjtLineList = Lists.newArrayList();
            List<WmsInventory> inventoryAddList = Lists.newArrayList();
            List<WmsInventory> inventoryUpdateList = Lists.newArrayList();
            List<WmsIvTransaction> ivTransactionList = Lists.newArrayList();
            List<WmsIvSplit> ivSplitsList = Lists.newArrayList();
            List<Long> deleteIvidList = Lists.newArrayList();
            IAdjustStrategy adjustStrategy = adjustStrategyFactory.getAdjustStrategy(adjhType);

            /**库存调整-预处理*/
            List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition>
                    omsInventoryChangeWriteBackConditions = adjustStrategy.process(wmsAdjtHeader, adjtLineList, wmsInventories,
                    inventoryAddList, inventoryUpdateList,
                    deleteIvidList, ivTransactionList, ivSplitsList,
                    userInfo, nowWithUTC);

            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                /**数据状态处理*/
                /**库存调整单单头*/
                int countForAdjHeader = wmsAdjtHeaderService.insertSelective(wmsAdjtHeader);
                if (countForAdjHeader <= 0) {
                    ErrorCode.ADJT_ERROR_4003.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                }
                /**库存调整单明细*/
                int countForAdjtLine = wmsAdjtLineService.insertBatch(adjtLineList);
                if (countForAdjtLine != countForAdjtLine) {
                    ErrorCode.ADJT_ERROR_4005.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                }
                /**库存记录*/
                if (!inventoryAddList.isEmpty()) {
                    int countForInventoryAdd = wmsInventoryService.insertBatch(inventoryAddList);
                    if (countForInventoryAdd != inventoryAddList.size()) {
                        ErrorCode.ADJT_ERROR_4006.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                    }
                    /**库存历史记录*/
                    List<WmsInventoryHistory> wmsInventoryHistoryAddList = DataConvertUtil.parseDataAsArray(inventoryAddList, WmsInventoryHistory.class);
                    int countForInventoryHistoryAdd = wmsInventoryHistoryService.insertBatch(wmsInventoryHistoryAddList);
                    if (countForInventoryHistoryAdd != wmsInventoryHistoryAddList.size()) {
                        ErrorCode.ADJT_ERROR_4007.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                    }
                }

                /**库存修改记录*/
                if (!inventoryUpdateList.isEmpty()) {
                    int countForInventoryUpdate = wmsInventoryService.updateBatch(inventoryUpdateList);
                    if (countForInventoryUpdate != inventoryUpdateList.size()) {
                        ErrorCode.ADJT_ERROR_4008.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                    }
                }

                /**库存删除记录*/
                if (!deleteIvidList.isEmpty()) {
                    int countForInventoryDelete = wmsInventoryService.deleteByPrimaryKeys(deleteIvidList);
                    if (countForInventoryDelete != deleteIvidList.size()) {
                        //库存调整失败-库存记录删除失败，库位编码={0},产品id={1},调整类型={2}
                        ErrorCode.ADJT_ERROR_4009.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                    }
                }

                /**库存变更记录*/
                if (ivTransactionList.isEmpty()) {
                    ErrorCode.ADJT_ERROR_4010.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                }
                int countForIvTransactionAdd = wmsIvTransactionService.insertBatch(ivTransactionList);
                if (countForIvTransactionAdd != ivTransactionList.size()) {
                    ErrorCode.ADJT_ERROR_4011.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                }
                /**库存拆分记录*/
                if (ivSplitsList.isEmpty()) {
                    int countForSplitAdd = wmsIvSplitService.insertBatch(ivSplitsList);
                    if (countForSplitAdd != ivSplitsList.size()) {
                        ErrorCode.ADJT_ERROR_4012.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), adjhType);
                    }
                }

                // 异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)
                // 指令入库
                if (Objects.nonNull(omsInventoryChangeWriteBackConditions) && !omsInventoryChangeWriteBackConditions.isEmpty()) {
                    OmsInventoryChangeWriteBack omsInventoryChangeWriteBack = new OmsInventoryChangeWriteBack();
                    omsInventoryChangeWriteBack.setList(omsInventoryChangeWriteBackConditions);
                    WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsInventoryChangeWriteBack);
                    // MQ发送指令
                    wmsOmsInventoryAddWriteBackProducer.send(wmsIdInstantdirective);
                }
            }
        } catch (Exception e) {
            log.error(e.getCause().getMessage());
            ExceptionHelper.rethrowRuntimeException(e);
        } finally {
            LockMapUtil.cancelLock(lockKey, lockValue);
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

        conditionsBuilder.orderBy("wi.iv_id desc");
        Map<String, Object> paramMap = conditionsBuilder.build();
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
        WmsInventory wmsInventory = DataConvertUtil.parseDataAsObject(request, WmsInventory.class);
        List<WmsInventory> wmsInventories = wmsInventoryService.selectByLcCodeAndProductId(wmsInventory);
        if (Objects.isNull(wmsInventories) || wmsInventories.isEmpty()) {
            ErrorCode.INVENTORY_DETAIL_ERROR_4024.throwError(request.getLcCode(), request.getProductId());
        }
        Map<Long, WmsInventory> ivId2EntityMap = wmsInventories.stream().collect(Collectors.toMap(WmsInventory::getIvId, Function.identity()));
        List<InventoryDetailResponse.InventoryDetailCondition> dataList = DataConvertUtil.parseDataAsArray(wmsInventories, InventoryDetailResponse.InventoryDetailCondition.class);
        dataList.stream().forEach(item -> {
            Long ivId = item.getIvId();
            WmsInventory inventory = ivId2EntityMap.get(ivId);
            BigDecimal[] bagRet = inventory.getBigBagExtraQty().divideAndRemainder(inventory.getMidBagRate());
            item.setMidBagQty(bagRet[0]);
            item.setTinyBagQty(bagRet[1]);
        });
        return DataConvertUtil.parseDataAsObject(dataList, InventoryDetailBussinessResponse.class);
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

        if (StringUtils.isNoneBlank(request.getInventoryPageQueryCondition().getLcType())) {
            conditionsBuilder.eq("lcType", request.getInventoryPageQueryCondition().getLcType());
        }

        conditionsBuilder.orderBy("wa.create_time desc");
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsAdjtHeaderService.selectCustomPage(pagination, paramMap);
        /**计算调整后数量**/
        page.getContent();
        InventoryAdjustPageQueryBussinessResponse response = new InventoryAdjustPageQueryBussinessResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 调整单类型
     */
    private enum AdjustType {
        INCREASE,
        LESSS,
        MOVE;

        public static boolean match(String adjustType) {
            for (AdjustType value : AdjustType.values()) {
                if (value.name().equals(adjustType)) {
                    return true;
                }
            }
            return false;
        }
    }


    @Data
    @AllArgsConstructor
    public static  class Person{
        private String name;
        private String type;
        private Integer age;
    }

    public static void main(String[] args) {
        List<Person> list = Lists.newArrayList();
        list.add(new Person("k1","man",1));
        list.add(new Person("k1","man",2));
        list.add(new Person("k2","man",2));
        list.add(new Person("k1","woman",3));
        list.add(new Person("k2","woman",4));

        Map<String, Integer> result = list.stream().collect(Collectors.groupingBy(item -> item.getName() + "_" + item.getType(), Collectors.summingInt(Person::getAge)));
        System.out.println(JSON.toJSONString(result,true));
    }
}
