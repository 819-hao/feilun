package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkListRequest;
import com.seeease.flywheel.storework.result.StoreWorkListResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 3号楼待收货列表
 * <a href="https://mastergo.com/file/95699402269098?page_id=148%3A3668&shareId=95699402269098">
 *     原型
 *  </a>
 *  新增sql  <ALTER TABLE bill_sale_return_order_line ADD COLUMN remark varchar(255)  COMMENT '备注';>
 */
@Service
@Extension(bizId = BizCode.SHOP_B3, useCase = UseCase.RECEIVING_LIST)
public class ShopB3WorkDeliveryListQueryExt implements QueryExtPtI<B3SaleReturnOrderListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;

    @Override
    public QueryResult query(QueryCmd<B3SaleReturnOrderListRequest> cmd) {
        B3SaleReturnOrderListRequest request = cmd.getRequest();
        PageResult<B3SaleReturnOrderListResult> result = facade.b3Page(request);
        List<QuerySingleResult> resultList;
        if (request.getStatus() == 2){
            resultList = result.getResult().stream()
                    .map(t -> QuerySingleResult.builder()
                            .result(t)
                            .task(UserTaskDto.builder()
                                    .businessKey(t.getSerialNo())
                                    .build())
                            .build())
                    .collect(Collectors.toList());
        }else {
            resultList =  result.getResult().stream()
                    .map(t -> QuerySingleResult.builder()
                            .result(t)
                            .build())
                    .collect(Collectors.toList());
        }

        return QueryPageResult.builder()
                .totalCount(result.getTotalCount())
                .totalPage(result.getTotalPage())
                .resultList(resultList)
                .build();

    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<B3SaleReturnOrderListRequest> cmd) {
          return Lists.newArrayList(TaskDefinitionKeyEnum.SHOP_RECEIVING);
    }

    @Override
    public Class<B3SaleReturnOrderListRequest> getRequestClass() {
        return B3SaleReturnOrderListRequest.class;
    }

    @Override
    public void validate(QueryCmd<B3SaleReturnOrderListRequest> cmd) {

    }
}
