package com.seeease.flywheel.web;

import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.IPurchaseQueryFacade;
import com.seeease.flywheel.purchase.request.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/30 09:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillPurchaseTest {

    @Resource
    private IPurchaseFacade iPurchaseFacade;

    @Resource
    private IPurchaseQueryFacade iPurchaseQueryFacade;

    /**
     * 同行采购-批量
     */
    @Test
    public void thcgplCreate() {

        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
        purchaseCreateRequest.setPurchaseType(1);
        purchaseCreateRequest.setPurchaseMode(3);
        purchaseCreateRequest.setCustomerId(24);
        purchaseCreateRequest.setCustomerContactId(56);
        purchaseCreateRequest.setPurchaseSubjectId(12);
        purchaseCreateRequest.setDemanderStoreId(23);
        purchaseCreateRequest.setRemarks("测试");
        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));

        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();

        billPurchaseLineDto.setGoodsId(1000);
        billPurchaseLineDto.setStockSn("FN100");
        billPurchaseLineDto.setFiness("99新");
        billPurchaseLineDto.setSalesPriority(1);
        billPurchaseLineDto.setGoodsLevel("G1");
        billPurchaseLineDto.setRemarks("成色");
        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("stock_three", Arrays.asList(1, 2));
        billPurchaseLineDto.setAttachmentMap(map);

        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));

        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void thcgbhCreate() {

        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
        purchaseCreateRequest.setPurchaseType(1);
        purchaseCreateRequest.setPurchaseMode(2);
        purchaseCreateRequest.setCustomerId(24);
        purchaseCreateRequest.setCustomerContactId(56);
        purchaseCreateRequest.setPurchaseSubjectId(12);
        purchaseCreateRequest.setDemanderStoreId(23);
        purchaseCreateRequest.setApplyPaymentSerialNo("111111111");
        purchaseCreateRequest.setRemarks("测试");
        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));

        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();

        billPurchaseLineDto.setGoodsId(1000);
        billPurchaseLineDto.setStockSn("FN100");
        billPurchaseLineDto.setFiness("99新");
        billPurchaseLineDto.setSalesPriority(1);
        billPurchaseLineDto.setGoodsLevel("G1");
        billPurchaseLineDto.setRemarks("成色");
        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("stock_three", Arrays.asList(1, 2));
        billPurchaseLineDto.setAttachmentMap(map);

        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));

        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void grhshsCreate() {

        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
        purchaseCreateRequest.setPurchaseType(4);
        purchaseCreateRequest.setPurchaseMode(4);
        purchaseCreateRequest.setCustomerId(26);
        purchaseCreateRequest.setCustomerContactId(8);
        purchaseCreateRequest.setPurchaseSubjectId(4);
        purchaseCreateRequest.setDemanderStoreId(2);
        purchaseCreateRequest.setApplyPaymentSerialNo("SQCWDK20230308-00004");
        purchaseCreateRequest.setRemarks("个人回收仅回收");
        purchaseCreateRequest.setImgList(Arrays.asList("https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/o_1gqg39u6e1emfh60kdd1h551mnf12.png"));
        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));
        purchaseCreateRequest.setStoreId(1);
        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();

        billPurchaseLineDto.setGoodsId(4);
        billPurchaseLineDto.setStockSn("FNtf100");
        billPurchaseLineDto.setFiness("99新");
        billPurchaseLineDto.setSalesPriority(0);
        billPurchaseLineDto.setGoodsLevel("G1");
        billPurchaseLineDto.setRemarks("成色");
        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
        billPurchaseLineDto.setRecyclePrice(BigDecimal.valueOf(200.22));
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("stock_three", Arrays.asList(1, 2));
        billPurchaseLineDto.setAttachmentMap(map);

        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));

        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void grhgjshCreate() {

        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
        purchaseCreateRequest.setPurchaseType(5);
        purchaseCreateRequest.setPurchaseMode(4);
        purchaseCreateRequest.setCustomerId(26);
        purchaseCreateRequest.setCustomerContactId(8);
        purchaseCreateRequest.setPurchaseSubjectId(4);
        purchaseCreateRequest.setDemanderStoreId(2);
        purchaseCreateRequest.setRemarks("个人回购仅回收");
        purchaseCreateRequest.setImgList(Arrays.asList("https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/o_1gqg39u6e1emfh60kdd1h551mnf12.png"));
        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));
        purchaseCreateRequest.setStoreId(1);
        purchaseCreateRequest.setOriginSaleSerialNo("TOCXS20230309-00020");
        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();

        billPurchaseLineDto.setGoodsId(4);
        billPurchaseLineDto.setStockSn("个人回购仅回收");
        billPurchaseLineDto.setFiness("99新");
        billPurchaseLineDto.setSalesPriority(0);
        billPurchaseLineDto.setGoodsLevel("G1");
        billPurchaseLineDto.setRemarks("成色");
        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
        billPurchaseLineDto.setRecyclePrice(BigDecimal.valueOf(200.22));
        billPurchaseLineDto.setOriginStockId(1005686);
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("stock_three", Arrays.asList(1, 2));
        billPurchaseLineDto.setAttachmentMap(map);

        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));

        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void grhgzhCreate() {

        PurchaseCreateRequest purchaseCreateRequest = new PurchaseCreateRequest();
        purchaseCreateRequest.setPurchaseType(5);
        purchaseCreateRequest.setPurchaseMode(5);
        purchaseCreateRequest.setCustomerId(26);
        purchaseCreateRequest.setCustomerContactId(8);
        purchaseCreateRequest.setPurchaseSubjectId(4);
        purchaseCreateRequest.setDemanderStoreId(2);
        purchaseCreateRequest.setRemarks("个人回购仅回收");
        purchaseCreateRequest.setImgList(Arrays.asList("https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/o_1gqg39u6e1emfh60kdd1h551mnf12.png"));
        purchaseCreateRequest.setTotalPurchasePrice(BigDecimal.valueOf(100.22));
        purchaseCreateRequest.setStoreId(1);
        purchaseCreateRequest.setOriginSaleSerialNo("TOCXS20230309-00020");
        purchaseCreateRequest.setSaleSerialNo("TOCXS20230309-00026");
        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = new PurchaseCreateRequest.BillPurchaseLineDto();

        billPurchaseLineDto.setGoodsId(4);
        billPurchaseLineDto.setStockSn("个人回购仅回收");
        billPurchaseLineDto.setFiness("99新");
        billPurchaseLineDto.setSalesPriority(0);
        billPurchaseLineDto.setGoodsLevel("G1");
        billPurchaseLineDto.setRemarks("成色");
        billPurchaseLineDto.setPurchasePrice(BigDecimal.valueOf(100.22));
        billPurchaseLineDto.setRecyclePrice(BigDecimal.valueOf(200.22));
        billPurchaseLineDto.setOriginStockId(1005686);
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("stock_three", Arrays.asList(1, 2));
        billPurchaseLineDto.setAttachmentMap(map);

        purchaseCreateRequest.setDetails(Arrays.asList(billPurchaseLineDto));

        iPurchaseFacade.create(purchaseCreateRequest);
    }

    @Test
    public void list() {

        PurchaseListRequest purchaseListRequest = new PurchaseListRequest();
        purchaseListRequest.setPage(1);
        purchaseListRequest.setLimit(100);
        purchaseListRequest.setStartTime("2022-01-01 00:00:00");
        purchaseListRequest.setEndTime("2024-01-01 00:00:00");

        System.out.println(iPurchaseFacade.list(purchaseListRequest));
    }

    @Test
    public void details() {
        PurchaseDetailsRequest purchaseDetailsRequest = new PurchaseDetailsRequest();
        purchaseDetailsRequest.setId(2);
//        purchaseDetailsRequest.setSerialNo("CG20230201-00013");

        System.out.println(iPurchaseFacade.details(purchaseDetailsRequest));

    }

//    @Test
//    public void logList(){
//        PurchaseLogRequest request = new PurchaseLogRequest();
//        request.setPage(1);
//        request.setLimit(10);
//        System.out.println(iPurchaseFacade.logList(request));
//    }

    @Test
    public void logList() {
        PurchaseForSaleRequest request = new PurchaseForSaleRequest();
        request.setOriginSaleSerialNo("TOCXS20230314-00003");
        request.setOriginStockId(1005682);

        System.out.println(iPurchaseFacade.purchaseForSale(request));
    }

    @Test
    public void applySettlement() {
        PurchaseApplySettlementRequest request = new PurchaseApplySettlementRequest();
        request.setPurchaseId(57);
        request.setBankAccount("23456780876543212345678");
        request.setAccountName("海溢银行");
        request.setBank("阿里中心");

        iPurchaseFacade.applySettlement(request);

        System.out.println();
    }

    @Test
    public void changeRecycle() {
        PurchaseChangeRecycleRequest request = new PurchaseChangeRecycleRequest();
        request.setPurchaseId(51);
        request.setRecyclePrice(BigDecimal.valueOf(34567890));

        iPurchaseFacade.changeRecycle(request);

        System.out.println("-----");
    }

    @Test
    public void list3(){
        iPurchaseQueryFacade.queryBuyBack(PurchaseBuyBackRequest.builder().saleSerialNoList(Arrays.asList("TOCXS20230324-00002")).build());
    }
}
