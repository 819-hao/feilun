package com.seeease.flywheel.web;

import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.goods.request.StockQueryRequest;
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
public class BillStockTest {

    @Resource
    private IStockFacade facade;

    @Test
    public void list() {

        StockQueryRequest request = new StockQueryRequest();

        System.out.println(facade.queryByStockSn(request));
    }

    @Test
    public void list2() {

        StockListRequest request = new StockListRequest();
        request.setPage(1);
        request.setLimit(10);
        request.setStoreId(1);
        request.setUseScenario(StockListRequest.UseScenario.PURCHASE_RETURN);

        System.out.println(facade.listStock(request));
    }

    @Test
    public void list3() {

        StockListRequest request = new StockListRequest();
        request.setPage(1);
        request.setLimit(10);
        request.setStoreId(1);
        request.setUseScenario(StockListRequest.UseScenario.ALLOCATE);

        System.out.println(facade.listStock(request));
    }


}
