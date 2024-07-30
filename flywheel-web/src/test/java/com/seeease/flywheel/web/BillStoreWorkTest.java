package com.seeease.flywheel.web;

import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkEditRequest;
import com.seeease.flywheel.storework.request.StoreWorkInStorageRequest;
import com.seeease.flywheel.storework.request.StoreWorkListRequest;
import com.seeease.flywheel.storework.request.StoreWorkLogRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/4 13:52
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillStoreWorkTest {

    @Resource
    private IStoreWorkFacade storeWorkFacade;

    @Resource
    private IStoreWorkQueryFacade iStoreWorkQueryFacade;

    @Test
    public void list(){

        StoreWorkListRequest request = new StoreWorkListRequest();
        request.setPage(1);
        request.setLimit(100);

        System.out.println(iStoreWorkQueryFacade.listReceiving(request));
    }

    @Test
    public void list2(){

        StoreWorkLogRequest request = new StoreWorkLogRequest();
        request.setPage(1);
        request.setLimit(100);
        request.setBelongingStoreId(1);
        request.setOptType(10);

        System.out.println(iStoreWorkQueryFacade.logList(request));
    }

    @Test
    public void edit(){

        StoreWorkEditRequest request = new StoreWorkEditRequest();
        request.setWorkId(1);
        request.setCommoditySituation(1);
        request.setRemarks("wertyu");

        storeWorkFacade.edit(request);
    }

    @Test
    public void printLabel(){

        StoreWorkInStorageRequest storeWorkInStorageRequest = new StoreWorkInStorageRequest();

        storeWorkInStorageRequest.setWorkIds(Arrays.asList(272));
        storeWorkFacade.inStorage(storeWorkInStorageRequest);
        System.out.println();
    }

}
