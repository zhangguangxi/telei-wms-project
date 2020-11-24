package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.telei.wms.project.api.TeleiWmsApiApplication;
import com.telei.wms.project.api.endpoint.inventory.dto.InventoryAddBussinessRequest;
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

    @Test
    public void testAddInventory(){
        String json = "{\"list\":[{\"companyId\":1030901707577493504,\"iabId\":1045077563002587136,\"ivDocumentId\":1045077719336880128,\"ivDocumentlineId\":1045077719341074432,\"ivQty\":200,\"lcCode\":\"S01-01-11\",\"paoId\":1045077719336880128,\"paolId\":1045077719341074432,\"productId\":4718757008713451522,\"putawayUser\":\"张光喜\",\"roId\":1045077408450873344,\"rooId\":1045077562767706112,\"roolId\":1045077563002587137,\"warehouseCode\":\"ywck\",\"warehouseId\":1030907360064833536}],\"orderType\":\"01\"}";
        InventoryAddBussinessRequest request = JSON.parseObject(json, InventoryAddBussinessRequest.class);
        inventoryBussiness.addInventory(request);
    }

    @Test
    public void testMoveInventory(){
        String json = "{\"companyId\":\"1030901707577493504\",\"ivQty\":2000,\"ivQtyAdjt\":2000,\"lcCode\":\"S01-01-21\",\"lcCodeAdjt\":\"S01-01-22\",\"productId\":4718049436532475906,\"reason\":\"样品库位移库\",\"warehouseCode\":\"ywck\",\"warehouseId\":1030907360064833536}";
        InventoryShiftBussinessRequest request = JSON.parseObject(json, InventoryShiftBussinessRequest.class);
        inventoryBussiness.shiftInventory(request);
    }


}
