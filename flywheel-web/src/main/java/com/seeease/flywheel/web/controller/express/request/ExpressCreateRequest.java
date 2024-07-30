package com.seeease.flywheel.web.controller.express.request;

import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.core.AccessToken;
import com.seeease.flywheel.sale.result.PrintOptionResult;
import com.seeease.flywheel.sf.result.ExpressOrderCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkDetailResult;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.DouyinPrintMapping;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/1 16:29
 */
@Data
@Builder
public class ExpressCreateRequest implements Serializable {

    private LogisticsNewCreateOrderRequest logisticsNewCreateOrderRequest;

    private AccessToken accessToken;

    //业务数据

    /**
     * 业务数据
     */
    private PrintOptionResult printOptionResult;

    private String requestId;

    private String requestID;

    private String taskID;

    private String documentID;

    private ExpressOrderCreateResult expressOrderCreateResult;

    private List<StoreWorkDetailResult> resultList;

    private DouyinPrintMapping douyinPrintMapping;

    private DouYinShopMapping douYinShopMapping;
}
