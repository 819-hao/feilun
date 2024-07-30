package com.seeease.flywheel.serve.sf.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.serve.sf.convert.ExpressOrderConverter;
import com.seeease.flywheel.serve.sf.entity.ExpressOrder;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderSourceEnum;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderStateEnum;
import com.seeease.flywheel.serve.sf.service.ExpressOrderService;
import com.seeease.flywheel.sf.IExpressOrderFacade;
import com.seeease.flywheel.sf.request.ExpressOrderCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderEditRequest;
import com.seeease.flywheel.sf.request.ExpressOrderQueryRequest;
import com.seeease.flywheel.sf.result.ExpressOrderCreateResult;
import com.seeease.flywheel.sf.result.ExpressOrderEditResult;
import com.seeease.flywheel.sf.result.ExpressOrderQueryResult;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:36
 */
@DubboService(version = "1.0.0")
@Slf4j
public class ExpressOrderFacade implements IExpressOrderFacade {

    @Resource
    private ExpressOrderService service;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpressOrderCreateResult create(ExpressOrderCreateRequest request) {

        ExpressOrder expressOrder = ExpressOrderConverter.INSTANCE.convertExpressOrderCreateRequest(request);
        expressOrder.setStoreId(UserContext.getUser().getStore().getId());
        expressOrder.setExpressState(ExpressOrderStateEnum.INIT);
        expressOrder.setExpressSource(ExpressOrderSourceEnum.TH_CG);

        service.saveOrUpdate(expressOrder, Wrappers.<ExpressOrder>lambdaUpdate()
                .eq(ExpressOrder::getSerialNo, request.getSerialNo())
                .in(ExpressOrder::getExpressState, ExpressOrderStateEnum.INIT, ExpressOrderStateEnum.FAIL)
        );

        return ExpressOrderCreateResult.builder().id(expressOrder.getId()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpressOrderEditResult edit(ExpressOrderEditRequest request) {

        ExpressOrder expressOrder = ExpressOrderConverter.INSTANCE.convertExpressOrderEditRequest(request);

        service.updateById(expressOrder);

        return ExpressOrderEditResult.builder().build();
    }

    @Override
    public ExpressOrderQueryResult query(ExpressOrderQueryRequest request) {

        List<ExpressOrder> list = service.list(Wrappers.<ExpressOrder>lambdaQuery()
                .eq(ExpressOrder::getSerialNo, request.getSerialNo())
                .or()
                .eq(ExpressOrder::getExpressNo, request.getExpressNo())
        );

        return ExpressOrderQueryResult.builder().list(list.stream().map(expressOrder -> ExpressOrderConverter.INSTANCE.convertExpressOrder(expressOrder)).collect(Collectors.toList())).build();
    }
}
