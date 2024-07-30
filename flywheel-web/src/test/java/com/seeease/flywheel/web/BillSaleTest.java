package com.seeease.flywheel.web;

import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderListRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/4 13:52
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillSaleTest {

    @Resource
    private ISaleOrderFacade iSaleOrderFacade;

    @Test
    public void list() {

        SaleOrderListRequest request = new SaleOrderListRequest();
        request.setPage(1);
        request.setLimit(100);

        System.out.println(iSaleOrderFacade.list(request));
    }

    @Test
    public void create() {

        SaleOrderCreateRequest request = new SaleOrderCreateRequest();

        request.setSaleType(2);
        request.setSaleSource(301);
        request.setSaleChannel(1);
        request.setSaleMode(1);
        request.setSaleNumber(1);
        request.setFirstSalesman(1);
        request.setCustomerContactId(16397);
        request.setCustomerId(17006);
        request.setPaymentMethod(1);
        request.setBuyCause(1);
        request.setShopId(1);
        request.setDeliveryLocationId(1);

        SaleOrderCreateRequest.BillSaleOrderLineDto billSaleOrderLineDto = new SaleOrderCreateRequest.BillSaleOrderLineDto();
        billSaleOrderLineDto.setGoodsId(4);
        billSaleOrderLineDto.setStockId(1005716);
        billSaleOrderLineDto.setConsignmentPrice(BigDecimal.valueOf(100000L));
        billSaleOrderLineDto.setClinchPrice(BigDecimal.valueOf(1050000L));
        billSaleOrderLineDto.setStrapMaterial("皮");

        BuyBackPolicyInfo buyBackPolicyMapper = new BuyBackPolicyInfo();

        buyBackPolicyMapper.setBuyBackTime(12);
        buyBackPolicyMapper.setDiscount(BigDecimal.valueOf(9));
        buyBackPolicyMapper.setPriceThreshold((20000));
        buyBackPolicyMapper.setReplacementDiscounts(BigDecimal.valueOf(0.5));
        buyBackPolicyMapper.setType(1);

        BuyBackPolicyInfo buyBackPolicyMapper2 = new BuyBackPolicyInfo();

        buyBackPolicyMapper2.setBuyBackTime(24);
        buyBackPolicyMapper2.setDiscount(BigDecimal.valueOf(8));
        buyBackPolicyMapper2.setPriceThreshold((20000));
        buyBackPolicyMapper2.setReplacementDiscounts(BigDecimal.valueOf(0.5));
        buyBackPolicyMapper2.setType(1);

        billSaleOrderLineDto.setBuyBackPolicy(Arrays.asList(buyBackPolicyMapper, buyBackPolicyMapper2));

        billSaleOrderLineDto.setIsCounterPurchase(1);
        billSaleOrderLineDto.setIsRepurchasePolicy(1);
        billSaleOrderLineDto.setLocationId(1);

        request.setDetails(Arrays.asList(billSaleOrderLineDto));

        iSaleOrderFacade.create(request);
    }


}
