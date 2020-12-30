package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.telei.wms.project.api.TeleiWmsApiApplication;
import com.telei.wms.project.api.endpoint.backlc.BaclcBussiness;
import com.telei.wms.project.api.endpoint.inventory.dto.InventoryAddBussinessRequest;
import com.telei.wms.project.api.endpoint.inventory.dto.InventoryDetailBussinessRequest;
import com.telei.wms.project.api.endpoint.inventory.dto.InventoryShiftBussinessRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: leo
 * @date: 2020/11/23 19:41
 */
@SpringBootTest(classes = TeleiWmsApiApplication.class)
public class InventoryBussinessTest {
    @Autowired
    private InventoryBussiness inventoryBussiness;

    @Autowired
    private BaclcBussiness baclcBussiness;

    @Test
    public void testAddInventory(){
        String json = "{\"list\":[{\"companyId\":1030901707577493504,\"iabId\":1050430475597776896,\"ivDocumentId\":1050431156677249024,\"ivDocumentlineId\":1050431156681443328,\"ivQty\":400,\"lcCode\":\"S01-02-22\",\"paoId\":1050431156677249024,\"paolId\":1050431156681443328,\"productId\":4721028922144196610,\"putawayUser\":\"张光喜\",\"roId\":4721271325430122497,\"rooId\":1050430475367090176,\"roolId\":1050430475597776897,\"warehouseCode\":\"ywck\",\"warehouseId\":1030907360064833536}],\"orderType\":\"01\"}";
        InventoryAddBussinessRequest request = JSON.parseObject(json, InventoryAddBussinessRequest.class);
        inventoryBussiness.addInventory(request);
    }




    @Test
    public void testMoveInventory(){
        String json = "{\"companyId\":\"1030901707577493504\",\"ivQty\":2000,\"ivQtyAdjt\":2000,\"lcCode\":\"S01-01-21\",\"lcCodeAdjt\":\"S01-01-22\",\"productId\":4718049436532475906,\"reason\":\"样品库位移库\",\"warehouseCode\":\"ywck\",\"warehouseId\":1030907360064833536}";
        InventoryShiftBussinessRequest request = JSON.parseObject(json, InventoryShiftBussinessRequest.class);
        inventoryBussiness.shiftInventory(request);
    }


    @Test
    public void testDetailInventory(){
        String json = "{\"companyId\":1051506761678194688,\"lcCode\":\"S99-01-11\",\"productId\":1053625327256864769,\"warehouseId\":1051515954409768960}";
        InventoryDetailBussinessRequest request = JSON.parseObject(json, InventoryDetailBussinessRequest.class);
        System.out.println(JSON.toJSONString(inventoryBussiness.detailInventory(request)));
    }
}
