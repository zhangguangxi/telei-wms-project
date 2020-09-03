package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.inventoryAddWriteBack.OmsInventoryAddWriteBack;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryAddWriteBackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryChangeWriteBackProducer;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: leo
 * @date: 2020/8/26 09:35
 */
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
    private WmsIvAttributebatchService  wmsIvAttributebatchService;

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



    /**
     * 入库(上架)
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryAddBussinessResponse addInventory(InventoryAddBussinessRequest request) {
        List<InventoryAddBussinessRequest.InventoryAddRequestCondition> requestList = request.getList();
        if(requestList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        //1.基本信息提取
        //1.1 基本信息提取-上架单单头id集合
        List<Long> requestPaoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaoId).collect(Collectors.toList());
        if(Objects.isNull(requestPaoIdList) || requestPaoIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020.throwError();
        }
        //1.2 基本信息提取-上架单明细id集合
        List<Long> requestPaolIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId).collect(Collectors.toList());
        if(Objects.isNull(requestPaolIdList) || requestPaolIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006.throwError();
        }
        //1.3 基本信息提取-库存批次id集合
        List<Long> requestIabIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIabId).collect(Collectors.toList());
        if(Objects.isNull(requestIabIdList) || requestIabIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004.throwError();
        }
        //1.4 基本信息提取-收货单单头id集合
        List<Long> requestRooIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId).collect(Collectors.toList());
        if(Objects.isNull(requestRooIdList) || requestRooIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }
        //1.5 基本信息提取-收货单明细集合
        List<Long> requestRooLIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoolId).collect(Collectors.toList());
        if(Objects.isNull(requestRooLIdList) || requestRooLIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021.throwError();
        }
        //1.6 基本信息提取-入库任务单头id集合
        List<Long> requestRoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId).collect(Collectors.toList());
        if(Objects.isNull(requestRoIdList) || requestRoIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010.throwError();
        }
        //1.8 基本信息提取-产品id集合
        List<Long> requestProductIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getProductId).collect(Collectors.toList());
        if(Objects.isNull(requestProductIdList) || requestProductIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002.throwError();
        }

        //获取锁-相关单据单头作为参数(上架单单头、库存批次单头、收货单单头、入库单头);
        List<List<Long>> lockKey = Arrays.asList(requestPaoIdList, requestIabIdList, requestRooIdList, requestRoIdList);
        Object lockValue = LockMapUtil.tryLock(lockKey);
        try{
        List<WmsInventory> inventoryList = DataConvertUtil.parseDataAsArray(request, WmsInventory.class);
        //2公用信息提取
        //2.1 UTC时间
        Date nowWithUTC = DateUtils.nowWithUTC();
        //2.2 用户信息(请求上下文)
        UserInfo userInfo = CustomRequestContext.getUserInfo();

        //3、预处理
        //3.1 产品(商品)-预处理
        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        productDetailRequest.setIds(requestProductIdList);
        ApiResponse productDetailResponse = productFeignClient.getProductDetailByBarCode(productDetailRequest);
        ProductListResponse response = JSON.parseObject(JSON.toJSONString(productDetailResponse.getData()), ProductListResponse.class);
        List<ProductDetailResponse> productList = response.getProductList();
        if(Objects.isNull(productList) || productList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003.throwError();
        }
        Map<Long, ProductDetailResponse> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getId, Function.identity()));

        //2.2 库存批次-预处理
        List<WmsIvAttributebatch> ivAttributebatches = wmsIvAttributebatchService.selectByPrimaryKeys(requestIabIdList);
        if(Objects.isNull(ivAttributebatches) || ivAttributebatches.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005.throwError();
        }
        Map<Long, WmsIvAttributebatch> ivAttributeBatchMap = ivAttributebatches.stream().collect(Collectors.toMap(WmsIvAttributebatch::getId, Function.identity()));

        //2.3 上架单单头-预处理(上架单单头 上架时间、上架用户、已上架数量)
        Long paoId = inventoryList.get(0).getIvDocumentId();
        WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(paoId);
        BigDecimal waitPutawayQty = inventoryList.stream().filter(item -> ((Objects.nonNull(item.getIvQty())) && item.getIvQty().intValue() > 0)).map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        wmsPaoHeader.setPutawayQty(wmsPaoHeader.getPutawayQty().add(waitPutawayQty));

        //2.4 上架单明细-预处理(上架时间、上架用户、上架数量)
        Map<Long, WmsInventory> paoLineMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getIvDocumentlineId, Function.identity()));
        if(Objects.isNull(paoLineMap) || paoLineMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        List<WmsPaoLine> paoLineList = wmsPaoLineService.selectByPrimaryKeys(requestPaolIdList);
        paoLineList.stream().forEach(item ->{
            item.setPaolQty(item.getPaolQty().add(paoLineMap.get(item).getIvQty()));
            item.setPutawayTime(nowWithUTC);
            item.setPutawayUser(userInfo.getUserName());
        });

        //2.5 收货单单头-预处理(更新 putawayQty)
        Map<Long, BigDecimal> rooQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
        if(Objects.isNull(rooQtyMap) || rooQtyMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }

        List<WmsRooHeader> rooHeaderList = wmsRooHeaderService.selectByPrimaryKeys(requestRooIdList);
        if(Objects.isNull(rooHeaderList) || rooHeaderList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008.throwError();
        }
        rooHeaderList.forEach(item ->{
               item.setPutawayQty(item.getPutawayQty().add(rooQtyMap.get(item.getId())));
        });

        //2.6 入库任务单头-预处理(上架数量累加:putawayQty 上架完成时间:putawayAllTime(when=putawayQty>=totalQty)  总计划入库数量:totalQty)
        Map<Long, BigDecimal> roQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
        if(Objects.isNull(roQtyMap) || roQtyMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError();
        }
        List<Long> roIdList = requestRoIdList;
        List<WmsRoHeader> roHeaderList = wmsRoHeaderService.selectByPrimaryKeys(roIdList);
        if(Objects.isNull(roHeaderList) || roHeaderList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError();
        }

        roHeaderList.stream().forEach(item ->{
            item.setPutawayQty(item.getPutawayQty().add(roQtyMap.get(item.getId())));
            if(item.getTotalQty().compareTo(item.getPutawayQty()) >= 0){
                item.setPutawayAllTime(nowWithUTC);
            }
        });

        //2.7 oms-入库计划单(单头/明细)-预处理 oms-采购单(单头/明细)-预处理
        List<Long> rolIdList = paoLineList.stream().map(WmsPaoLine::getRolId).collect(Collectors.toList());
        if(Objects.isNull(rolIdList) || rolIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012.throwError();
        }
        List<WmsRoLine> roLineList = wmsRoLineService.selectByPrimaryKeys(rolIdList);
        if(Objects.isNull(roLineList) || roLineList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013.throwError();
        }
        Map<Long, WmsPaoLine> paoLineRolIdToEntityMap = paoLineList.stream().collect(Collectors.toMap(WmsPaoLine::getRolId, Function.identity()));

        List<OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition> omsInventoryAddWriteBackConditionList = new ArrayList<>();
        roLineList.stream().forEach(item ->{
            OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition omsInventoryAddWriteBackCondition = new OmsInventoryAddWriteBack.OmsInventoryAddWriteBackCondition();
            omsInventoryAddWriteBackCondition.setPodId(item.getPodId());
            omsInventoryAddWriteBackCondition.setPoId(item.getPoId());
            omsInventoryAddWriteBackCondition.setRpdId(item.getRpdId());
            omsInventoryAddWriteBackCondition.setIvQty(paoLineRolIdToEntityMap.get(item.getId()).getPaolQty());
            omsInventoryAddWriteBackConditionList.add(omsInventoryAddWriteBackCondition);
        });
        OmsInventoryAddWriteBack omsInventoryAddWriteBack = new OmsInventoryAddWriteBack();
        omsInventoryAddWriteBack.setList(omsInventoryAddWriteBackConditionList);

        //2.8 库存记录-预处理
        inventoryList.stream().forEach(item -> {
            ProductDetailResponse productDetail = productMap.get(item.getProductId());//产品记录
            WmsIvAttributebatch ivAttributebatchDetail = ivAttributeBatchMap.get(item.getIabId());//批次记录
            BigDecimal bigBagRate = new BigDecimal(ivAttributebatchDetail.getBigBagRate());//大包转转换率(批次表记录)
            BigDecimal midBagRate = new BigDecimal(ivAttributebatchDetail.getMidBagRate());//中包转化数
            BigDecimal ivQty = item.getIvQty();//库存数量

            item.setId(idGenerator.getUnique());//主键id
            item.setIvFifoTime(ivAttributebatchDetail.getCreateTime());//先进先出时间(获取跑批次表的创建时间)
            item.setStockUnit(productDetail.getStockUnit());;//计量单位
            item.setBigBagRate(bigBagRate);//大包转换率  批次表中获取
            item.setBigBagQty(ivQty.multiply(bigBagRate));//大包数量
            item.setBigBagExtraQty(ivQty.divideAndRemainder(bigBagRate)[1]);//大包剩余数量
            item.setMidBagRate(midBagRate); // 中包转化数(批次表记录)
            item.setMidBagQty(ivQty.multiply(midBagRate));//中包数量
            item.setMidBagExtraQty(ivQty.divideAndRemainder(midBagRate)[1]);//中包剩余数量
            item.setLastupdateUser(userInfo.getAccountId());//最近库存更新用户(上下文用户)
            item.setIvTranstime(nowWithUTC);//最近库存更新时间
            item.setIvCreatetime(nowWithUTC);//创建时间
            item.setBizDate(nowWithUTC);//业务日期(与创建时间保持一致)
        });

        //3、处理(状态数据变更)
        if(LockMapUtil.confirmLock(lockKey,lockValue)){
            inventoryDataProcess(inventoryList, wmsPaoHeader, paoLineList, rooHeaderList, roHeaderList, omsInventoryAddWriteBack);
        }
        }catch (Exception e){
            throw e;
        }finally {
            LockMapUtil.cancelLock(lockKey,lockValue);
        }

        return new InventoryAddBussinessResponse();
    }

    /**
     *  入库相关数据处理
     * @param inventoryList
     * @param wmsPaoHeader
     * @param paoLineList
     * @param rooHeaderList
     * @param roHeaderList
     * @param omsInventoryAddWriteBack
     */
    private void inventoryDataProcess(List<WmsInventory> inventoryList, WmsPaoHeader wmsPaoHeader, List<WmsPaoLine> paoLineList, List<WmsRooHeader> rooHeaderList, List<WmsRoHeader> roHeaderList, OmsInventoryAddWriteBack omsInventoryAddWriteBack) {
        //3.1 库存/库存历史处理  插入wms_inventory wms_inventory_history
        int countForInventory = wmsInventoryService.insertBatch(inventoryList);
        if(countForInventory != inventoryList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4014.throwError();
        }
        List<WmsInventoryHistory> inventoryHistoryList = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventoryHistory.class);
        int countForInventoryHistory = wmsInventoryHistoryService.insertBatch(inventoryHistoryList);
        if(countForInventoryHistory != inventoryHistoryList.size() ){
            ErrorCode.INVENTORY_ADD_ERROR_4015.throwError();
        }
        // TODO: 2020/9/2 wms_inventory_transaction
        //3.2 更新上架单单头/上架单明细
        int countForPao = wmsPaoHeaderService.updateByPrimaryKey(wmsPaoHeader);
        if(countForPao <= 0){
            ErrorCode.INVENTORY_ADD_ERROR_4016.throwError();
        }
        int countForPaoLine = wmsPaoLineService.updateBatch(paoLineList);
        if(countForPaoLine != paoLineList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4017.throwError();
        }
        //2.2 更新收货单单头
        int countForRootHeader = wmsRooHeaderService.updateBatch(rooHeaderList);
        if(countForRootHeader != rooHeaderList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4018.throwError();
        }
        //2.3 更新入库任务单头
        int countForRoHeader = wmsRoHeaderService.updateBatch(roHeaderList);
        if(countForRoHeader != roHeaderList.size()){
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
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryIncreaseBussinessResponse increaseInventory(InventoryIncreaseBussinessRequest request) {
        /**基础信息*/
        Date nowWithUTC = DateUtils.nowWithUTC();
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        long adjtId = idGenerator.getUnique();
        long adjtlId = idGenerator.getUnique();
        String bussinessNumber = getBussinessNumber("wms");

        /**库存调整单单头*/
        WmsAdjtHeader wmsAdjtHeader = DataConvertUtil.parseDataAsObject(request, WmsAdjtHeader.class);
        wmsAdjtHeader.setAdjhCode(bussinessNumber);
        wmsAdjtHeader.setId(adjtId);
        wmsAdjtHeader.setBizDate(nowWithUTC);
        wmsAdjtHeader.setCreateTime(nowWithUTC);
        wmsAdjtHeader.setCreateUser(userInfo.getUserName());
        wmsAdjtHeader.setAdjhType("KC");
        wmsAdjtHeader.setIvihStatus("20");

        /**库存调整单明细*/
        WmsAdjtLine wmsAdjtLine = DataConvertUtil.parseDataAsObject(request, WmsAdjtLine.class);
        wmsAdjtLine.setId(adjtlId);
        wmsAdjtLine.setAdjhId(adjtId);
        wmsAdjtLine.setIvAdjhType("INCR");

        /**数据状态处理*/
        int countForAdjHeader = wmsAdjtHeaderService.insertSelective(wmsAdjtHeader);
        if(countForAdjHeader <= 0){
            ErrorCode.ADJT_INCREASE_ERROR_4001.throwError();
        }
        int countForAdjLine = wmsAdjtLineService.insertSelective(wmsAdjtLine);
        if(countForAdjLine <= 0){
            ErrorCode.ADJT_INCREASE_ERROR_4002.throwError();
        }
        return new InventoryIncreaseBussinessResponse();
    }


    /***
     * 库存-调少
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryReduceBussinessResponse reduceInventory(InventoryReduceBussinessRequest request) {
        /**基础信息*/
        Date nowWithUTC = DateUtils.nowWithUTC();
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        long adjtId = idGenerator.getUnique();
        long adjtlId = idGenerator.getUnique();
        String bussinessNumber = getBussinessNumber("wms");

        /**库存调整单单头*/
        WmsAdjtHeader wmsAdjtHeader = DataConvertUtil.parseDataAsObject(request, WmsAdjtHeader.class);
        wmsAdjtHeader.setAdjhCode(bussinessNumber);
        wmsAdjtHeader.setId(adjtId);
        wmsAdjtHeader.setBizDate(nowWithUTC);
        wmsAdjtHeader.setCreateTime(nowWithUTC);
        wmsAdjtHeader.setCreateUser(userInfo.getUserName());
        wmsAdjtHeader.setAdjhType("KC");
        wmsAdjtHeader.setIvihStatus("20");

        /**库存调整单明细*/
        WmsAdjtLine wmsAdjtLine = DataConvertUtil.parseDataAsObject(request, WmsAdjtLine.class);
        wmsAdjtLine.setId(adjtlId);
        wmsAdjtLine.setAdjhId(adjtId);
        wmsAdjtLine.setIvAdjhType("LESS");

        /**数据状态处理*/
        int countForAdjHeader = wmsAdjtHeaderService.insertSelective(wmsAdjtHeader);
        if(countForAdjHeader <= 0){
            ErrorCode.ADJT_REDUCE_ERROR_4003.throwError();
        }
        int countForAdjLine = wmsAdjtLineService.insertSelective(wmsAdjtLine);
        if(countForAdjLine <= 0){
            ErrorCode.ADJT_REDUCE_ERROR_4004.throwError();
        }
        return new InventoryReduceBussinessResponse();
    }


    /**
     * 库存调整-审核
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryReviewBussinessResponse reviewInventory(InventoryReviewBussinessRequest request) {
        Long adjhId = request.getAdjhId();
        WmsAdjtHeader wmsAdjtHeader = wmsAdjtHeaderService.selectByPrimaryKey(adjhId);
        if(Objects.isNull(wmsAdjtHeader)){
            // TODO: 2020/9/2
        }
        WmsAdjtLine wmsAdjtLine = new WmsAdjtLine();
        wmsAdjtLine.setAdjhId(adjhId);
        List<WmsAdjtLine> adjtLineList = wmsAdjtLineService.selectByEntity(wmsAdjtLine);
        if(Objects.isNull(adjtLineList) || adjtLineList.isEmpty()){
            // TODO: 2020/9/2
        }
        List<Long> ivIdList = adjtLineList.stream().map(WmsAdjtLine::getIvId).collect(Collectors.toList());
        List<WmsInventory> inventoryList = wmsInventoryService.selectByPrimaryKeys(ivIdList);
        if(Objects.isNull(inventoryList) || inventoryList.isEmpty()){
            // TODO: 2020/9/2
        }

//        Map<String, List<WmsAdjtLine>> adjtMap = adjtLineList.stream().collect(Collectors.groupingBy(WmsAdjtLine::getIvAdjhType));
        Map<Long, WmsAdjtLine> adjLineIvId2EntityMap = adjtLineList.stream().collect(Collectors.toMap(WmsAdjtLine::getIvId, Function.identity()));
        //数据组装、库存、库存历史、库存事务
        UserInfo currentUser = CustomRequestContext.getUserInfo();
        Date nowWithUTC = DateUtils.nowWithUTC();

//        List<WmsAdjtLine> incrList = adjtMap.get("INCR");
//        List<WmsAdjtLine> lessList = adjtMap.get("LESS");
//        List<WmsAdjtLine> moveList = adjtMap.get("MOVE");
//
//        if(Objects.nonNull(incrList) &&  !incrList.isEmpty()){
//            incrList.stream().forEach(item ->{
//
//            });
//        }
//
//        if(Objects.nonNull(lessList) && !lessList.isEmpty()){
//
//        }
//
//        if(Objects.nonNull(moveList ) && !moveList.isEmpty()){
//
//        }


        Map<Long, WmsInventory> id2EntityMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getId, Function.identity()));
        List<WmsInventory> convertInventories = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventory.class);
        List<WmsInventory> waitDeleteList = new ArrayList<>();
        List<WmsInventory> waitAddList = new ArrayList<>();
        List<WmsInventory> waitUpdateList = new ArrayList<>();

        List<WmsInventory> waitMoveDeleteList = new ArrayList<>();
        List<WmsInventory> waitMoveAddList = new ArrayList<>();
        List<WmsInventory> waitMoveUpdateList = new ArrayList<>();
        List<WmsIvTransaction> transactionList = new ArrayList<>();
        List<WmsIvSplit> waitAddSplitList = new ArrayList<>();

        convertInventories.stream().forEach(item ->{
            WmsAdjtLine adjtLine = adjLineIvId2EntityMap.get(item.getId());
            BigDecimal frontIvQty = adjtLine.getIvQty();
            BigDecimal dbIvQty = item.getIvQty();
            //移库
            int compareRet = frontIvQty.compareTo(dbIvQty);
            if(compareRet < 0){
                // TODO: 2020/9/3
            }
            if(AdjustType.MOVE.name().equals(adjtLine.getIvAdjhType())){
                //完全移库,记录transaction
                if(compareRet == 0){
                    waitDeleteList.add(item);
                    // TODO: 2020/9/3 记录transaction
                }
                //部分移库 更新、新增、transaction
                if(compareRet > 0){
                    //更新，将from进行减库操作
                    WmsInventory updateItem = DataConvertUtil.parseDataAsObject(item, WmsInventory.class);
                    WmsInventory addItem = DataConvertUtil.parseDataAsObject(item, WmsInventory.class);
                    updateItem.setIvQty(dbIvQty.subtract(frontIvQty));
                    waitUpdateList.add(updateItem);
                    //记录transaction


                    //新增
                    addItem.setId(idGenerator.getUnique());
                    addItem.setIvQty(frontIvQty);
                    addItem.setBigBagQty(frontIvQty.multiply(addItem.getBigBagRate()));//大包数量
                    addItem.setBigBagExtraQty(frontIvQty.divideAndRemainder(addItem.getBigBagRate())[1]);//大包剩余数量
                    addItem.setMidBagQty(frontIvQty.multiply(addItem.getMidBagRate()));//中包数量
                    addItem.setMidBagExtraQty(frontIvQty.divideAndRemainder(addItem.getMidBagRate())[1]);//中包剩余数量
                    addItem.setLastupdateUser(currentUser.getAccountId());//最近库存更新用户(上下文用户)
                    addItem.setIvTranstime(nowWithUTC);//最近库存更新时间
                    addItem.setIvCreatetime(nowWithUTC);//创建时间
                    addItem.setBizDate(nowWithUTC);//业务日期(与创建时间保持一致)
                    waitAddList.add(addItem);

                    //记录split单
                    WmsIvSplit wmsIvSplit = new WmsIvSplit();


                }

            }else{
                //库存调多 更新、transaction 
                if(AdjustType.INCR.name().equals(adjtLine.getIvAdjhType())){
                    item.setIvId(idGenerator.getUnique());
                    item.setIvIdFrom(adjtLine.getId());
                    item.setIvQty(frontIvQty);
                    waitAddList.add(item);
                }else {
                    //库存调少
                    if(dbIvQty.subtract(frontIvQty).intValue() ==  0){
                        waitDeleteList.add(item);
                    }
                    //更新-库存减少
                    item.setIvQty(dbIvQty.subtract(frontIvQty));
                    waitUpdateList.add(item);
                }
            }
        });

        if(!waitAddList.isEmpty()){
            int countForIventoryAdd = wmsInventoryService.insertBatch(waitAddList);
            if(countForIventoryAdd != waitAddList.size()){
                // TODO: 2020/9/3
            }
            List<WmsInventoryHistory> wmsInventoryHistories = DataConvertUtil.parseDataAsArray(waitAddList, WmsInventoryHistory.class);
            int countForInventoryHistoryAdd = wmsInventoryHistoryService.insertBatch(wmsInventoryHistories);
            if(countForInventoryHistoryAdd != wmsInventoryHistories.size()){
                // TODO: 2020/9/3
            }
            addTransactionRecord(adjLineIvId2EntityMap, currentUser, nowWithUTC, waitAddList,"KC","INCR");

        }

        if(!waitUpdateList.isEmpty()){
            int countForUpdate = wmsInventoryService.updateBatch(waitUpdateList);
            if(countForUpdate != waitUpdateList.size()){
                // TODO: 2020/9/3
            }
            addTransactionRecord(adjLineIvId2EntityMap, currentUser, nowWithUTC, waitAddList,"KC","LESS");
        }

        if(!waitDeleteList.isEmpty()){
            List<Long> ivIds = waitDeleteList.stream().map(WmsInventory::getId).collect(Collectors.toList());
            int countForDelete = wmsInventoryService.deleteByPrimaryKeys(ivIds);
            if(countForDelete != ivIds.size()){
            }
            addTransactionRecord(adjLineIvId2EntityMap, currentUser, nowWithUTC, waitAddList,"KC","LESS");
        }

        //异步(MQ)回写oms的oms-inventory
        // 指令入库
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", "");
        // MQ发送指令
        wmsOmsInventoryChangeWriteBackProducer.send(wmsIdInstantdirective);

        return new InventoryReviewBussinessResponse();
    }

    /**
     * 库存变动记录
     * @param adjtLineIvId2EntityMap
     * @param currentUser
     * @param nowWithUTC
     * @param list
     * @param adjhType
     * @param ivtChangeType
     */
    private void addTransactionRecord(Map<Long, WmsAdjtLine> adjtLineIvId2EntityMap, UserInfo currentUser, Date nowWithUTC, List<WmsInventory> list,String adjhType,String ivtChangeType) {
        Map<Long, WmsInventory> ivId2EntityAddMap = list.stream().collect(Collectors.toMap(WmsInventory::getId, Function.identity()));
        List<WmsIvTransaction> ivTransactions = DataConvertUtil.parseDataAsArray(list, WmsIvTransaction.class);
        ivTransactions.stream().forEach(item ->{
            WmsInventory inventory = ivId2EntityAddMap.get(item.getId());
            WmsAdjtLine adjtLine = adjtLineIvId2EntityMap.get(item.getId());
            item.setApCode(adjhType);
            item.setLcCodeFrom(inventory.getLcCode());//原库位编码
            item.setIvQtyFrom(inventory.getIvQty());//库存调整前数量
            item.setIvQtyTo(adjtLine.getIvQty().add(inventory.getIvQty()));//库存调整后数量
            item.setIvtChangeType(ivtChangeType);
            item.setCreateTime(nowWithUTC);
            item.setCreateUser(currentUser.getUserName());
            if("MOVE".equals(ivtChangeType)){
                item.setLcCodeTo(adjtLine.getLcCode());//变动后的库位编码
                return;
            }
            item.setLcCodeTo("");//变动后的库位编码

        });
    }

    /**
     * 移库
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryShiftBussinessResponse shiftInventory(InventoryShiftBussinessRequest request) {
        /**基础信息*/
        Date nowWithUTC = DateUtils.nowWithUTC();
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        long adjtId = idGenerator.getUnique();
        long adjtlId = idGenerator.getUnique();
        String bussinessNumber = getBussinessNumber("wms");

        /**库存调整单单头*/
        WmsAdjtHeader wmsAdjtHeader = DataConvertUtil.parseDataAsObject(request, WmsAdjtHeader.class);
        wmsAdjtHeader.setAdjhCode(bussinessNumber);
        wmsAdjtHeader.setId(adjtId);
        wmsAdjtHeader.setBizDate(nowWithUTC);
        wmsAdjtHeader.setCreateTime(nowWithUTC);
        wmsAdjtHeader.setCreateUser(userInfo.getUserName());
        wmsAdjtHeader.setAdjhType("YW");
        wmsAdjtHeader.setIvihStatus("20");

        /**库存调整单明细*/
        WmsAdjtLine wmsAdjtLine = DataConvertUtil.parseDataAsObject(request, WmsAdjtLine.class);
        wmsAdjtLine.setId(adjtlId);
        wmsAdjtLine.setAdjhId(adjtId);
        wmsAdjtLine.setIvAdjhType("MOVE");

        /**数据状态处理*/
        int countForAdjHeader = wmsAdjtHeaderService.insertSelective(wmsAdjtHeader);
        if(countForAdjHeader <= 0){
            ErrorCode.ADJT_SHIFT_ERROR_4005.throwError();
        }
        int countForAdjLine = wmsAdjtLineService.insertSelective(wmsAdjtLine);
        if(countForAdjLine <= 0){
            ErrorCode.ADJT_SHIFT_ERROR_4006.throwError();
        }
        return new InventoryShiftBussinessResponse();
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
     * 调整单类型
     */
    private enum AdjustType{
        INCR,
        LESSS,
        MOVE;
        public static boolean  match(String adjustType){
            for (AdjustType value : AdjustType.values()) {
                if(value.name().equals(adjustType)){
                    return true;
                }
            }
            return false;
        }
    }
}
