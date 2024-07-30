package com.seeease.flywheel.web;

import com.seeease.flywheel.financial.IApplyFinancialPaymentFacade;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentQueryAllRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/30 09:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillApplyTest {

    @Resource
    private IApplyFinancialPaymentFacade facade;

    @Test
    public void list() {

        ApplyFinancialPaymentQueryAllRequest request = new ApplyFinancialPaymentQueryAllRequest();
        request.setPage(1);
        request.setLimit(100);

        System.out.println(facade.queryAll(request));
    }
    @Test
    public void list2() {

//        ApplyFinancialPaymentCreateRequest request = new ApplyFinancialPaymentCreateRequest();
//        //1
//        request.setTypePayment(1);
//        request.setPricePayment(BigDecimal.valueOf(2000));
//        request.setSubjectPayment(20);
//        facade.create(request);

        System.out.println();
    }


}
