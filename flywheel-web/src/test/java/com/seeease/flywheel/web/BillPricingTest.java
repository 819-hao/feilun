package com.seeease.flywheel.web;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingFinishRequest;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.pricing.request.PricingLogListRequest;
import com.seeease.flywheel.pricing.result.PricingListResult;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillPricingTest {

    @Resource
    private IPricingFacade facade;

    @Test
    public void list() {

        PricingListRequest request = new PricingListRequest();
        request.setPage(1);
        request.setLimit(100);
        PageResult<PricingListResult> list = facade.list(request);
        System.out.println(list);
        log.info("返回的定价列表信息为：",list.getResult());
    }

    @Test
    public void list2() {

        PricingLogListRequest request = new PricingLogListRequest();
        request.setPage(1);
        request.setLimit(100);

        System.out.println(facade.logList(request));
    }
    @Test
    public void list3() {

        PricingFinishRequest request = new PricingFinishRequest();
        request.setId(12);
        request.setSerialNo("DJ20230322-00006");
        request.setTocPrice(BigDecimal.valueOf(45000));
        request.setTobPrice(BigDecimal.valueOf(40000));

        System.out.println(facade.finish(request));
    }


}
