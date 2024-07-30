package com.seeease.flywheel.web;

import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.request.QualityTestingEditRequest;
import com.seeease.flywheel.qt.request.QualityTestingListRequest;
import com.seeease.flywheel.qt.request.QualityTestingWaitDeliverListRequest;
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
public class BillQtTest {

    @Resource
    private IQualityTestingFacade qualityTestingFacade;

    @Test
    public void list() {

        QualityTestingListRequest request = new QualityTestingListRequest();
        request.setPage(1);
        request.setLimit(100);
        request.setStockSn("234567");

        System.out.println(qualityTestingFacade.list(request));
    }

    @Test
    public void edit() {

        QualityTestingEditRequest request = new QualityTestingEditRequest();
        request.setQualityTestingId(1);
        request.setFiness("111");
        request.setWeek("99");
        request.setStrapMaterial("毛");
        request.setWatchSection("毛1");

        qualityTestingFacade.edit(request);
    }

    @Test
    public void decision() {
        QualityTestingDecisionRequest qualityTestingDecisionRequest = new QualityTestingDecisionRequest();

        qualityTestingDecisionRequest.setQualityTestingId(4);
//        qualityTestingDecisionRequest.setQualityTestingId(3);
//        qualityTestingDecisionRequest.setQtState(2);
        qualityTestingDecisionRequest.setQtState(3);
//        qualityTestingDecisionRequest.setExceptionReason("fghjk");
//        qualityTestingDecisionRequest.setExceptionReasonId(1);
        qualityTestingDecisionRequest.setFixAdvise("cess");
        qualityTestingDecisionRequest.setFixMoney(BigDecimal.valueOf(500.91));

        qualityTestingFacade.decision(qualityTestingDecisionRequest);
    }

    @Test
    public void batchPass() {

        qualityTestingFacade.batchPass(Arrays.asList(1));
    }

    @Test
    public void batchPass2() {
        QualityTestingWaitDeliverListRequest request = new QualityTestingWaitDeliverListRequest();

        request.setPage(1);
        request.setLimit(20);
        request.setDeliver(2);
        System.out.println(qualityTestingFacade.qtWaitDeliver(request));
    }
}
