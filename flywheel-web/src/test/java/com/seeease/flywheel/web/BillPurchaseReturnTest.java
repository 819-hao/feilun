package com.seeease.flywheel.web;

import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnDetailsRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/30 09:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillPurchaseReturnTest {

    @Resource
    private IPurchaseReturnFacade iPurchaseReturnFacade;



    @Test
    public void list(){

        PurchaseReturnListRequest purchaseReturnListRequest = new PurchaseReturnListRequest();
        purchaseReturnListRequest.setPage(1);
        purchaseReturnListRequest.setLimit(100);
        purchaseReturnListRequest.setCustomerName("帆");
        purchaseReturnListRequest.setPurchaseReturnState(1);
        purchaseReturnListRequest.setCreatedBy("SYSTEM_DEFAULT");
        purchaseReturnListRequest.setStartTime("2022-01-01 00:00:00");
        purchaseReturnListRequest.setEndTime("2024-01-01 00:00:00");

        System.out.println(iPurchaseReturnFacade.list(purchaseReturnListRequest));
    }

    @Test
    public void details(){
        PurchaseReturnDetailsRequest purchaseDetailsRequest = new PurchaseReturnDetailsRequest();
        purchaseDetailsRequest.setId(1);
        purchaseDetailsRequest.setStoreId(1);
//        purchaseDetailsRequest.setSerialNo("CG20230201-00013");

        System.out.println(iPurchaseReturnFacade.details(purchaseDetailsRequest));

    }


}
