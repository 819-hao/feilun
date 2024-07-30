package com.seeease.flywheel.web;

import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/30 09:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillFixTest {

    @Resource
    private IFixFacade facade;


    @Test
    public void create(){

//        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
//        purchaseCreateRequest.setPurchaseType(1);
//        purchaseCreateRequest.setPurchaseSource(1);
//        purchaseCreateRequest.setCustomerId(1);
//        purchaseCreateRequest.setCustomerContactId(1);
//        purchaseCreateRequest.setPurchaseSubjectId(12);
//        purchaseCreateRequest.setViaSubjectId(22);
//        purchaseCreateRequest.setDemanderStoreId(23);
//        purchaseCreateRequest.setRemarks("测试");
//        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));
//
//        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();
//
//        billPurchaseLineDto.setGoodsId(1000);
//        billPurchaseLineDto.setStockSn("FN100");
//        billPurchaseLineDto.setAttachmentList("单表");
//        billPurchaseLineDto.setFiness("99新");
//        billPurchaseLineDto.setSalesPriority(1);
//        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
//
//        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));


//        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void list(){

        FixListRequest request = new FixListRequest();
        request.setPage(1);
        request.setLimit(100);
//        request.setBrand("欧米茄");

        System.out.println(facade.list(request));
    }

    @Test
    public void details(){
        FixDetailsRequest request = new FixDetailsRequest();
        request.setId(1);
        System.out.println(facade.details(request));

    }

    @Test
    public void edit(){

        FixEditRequest fixEditRequest = new FixEditRequest();
        fixEditRequest.setFixId(1);
//        fixEditRequest.setFixType(1);
//        fixEditRequest.setFixDay(100);
//        fixEditRequest.setRemark("备注表");
//        fixEditRequest.setMaintenanceMasterId(1);
//
//        FixEditRequest.FixProjectMapper fixProjectMapper = new FixEditRequest.FixProjectMapper();
//        fixProjectMapper.setFixProjectId(6);
//        fixProjectMapper.setFixMoney(BigDecimal.valueOf(100.34));
//
//        FixEditRequest.FixProjectMapper fixProjectMapper2 = new FixEditRequest.FixProjectMapper();
//        fixProjectMapper2.setFixProjectId(6);
//        fixProjectMapper2.setFixMoney(BigDecimal.valueOf(100.36));
//        fixEditRequest.setContent(Arrays.asList(fixProjectMapper, fixProjectMapper2));
//        fixEditRequest.setDefectOrNot(0);
//        fixEditRequest.setDefectDescription("");
        fixEditRequest.setSpecialExpediting(1);

//        facade.edit(Arrays.asList(fixEditRequest));
    }

    @Test
    public void finish(){

        FixFinishRequest fixFinishRequest = new FixFinishRequest();
        fixFinishRequest.setFixId(2);

        facade.finish(fixFinishRequest);
    }

    @Test
    public void receive(){

        FixReceiveRequest.FixReceiveListRequest fixReceiveListRequest = new FixReceiveRequest.FixReceiveListRequest();

        fixReceiveListRequest.setFixId(2);

        facade.receive(Arrays.asList(fixReceiveListRequest));
    }
}
