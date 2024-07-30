package com.seeease.flywheel.serve.sf.rpc;

import com.seeease.flywheel.serve.sf.convert.ExpressOrderPrintConverter;
import com.seeease.flywheel.serve.sf.entity.ExpressOrderPrint;
import com.seeease.flywheel.serve.sf.service.ExpressOrderPrintService;
import com.seeease.flywheel.sf.IExpressOrderPrintFacade;
import com.seeease.flywheel.sf.request.ExpressOrderPrintCreateRequest;
import com.seeease.flywheel.sf.result.ExpressOrderPrintCreateResult;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/30 11:40
 */
@DubboService(version = "1.0.0")
@Slf4j
public class ExpressOrderPrintFacade implements IExpressOrderPrintFacade {

    @Resource
    private ExpressOrderPrintService service;

    @Override
    public ExpressOrderPrintCreateResult create(ExpressOrderPrintCreateRequest request) {
        ExpressOrderPrint expressOrderPrint = ExpressOrderPrintConverter.INSTANCE.convertExpressOrderPrintCreateRequest(request);
        expressOrderPrint.setStoreId(UserContext.getUser().getStore().getId());
        expressOrderPrint.setPrintSource(1);
        expressOrderPrint.setPrintTemplate(request.getPrintTemplate());

        service.save(expressOrderPrint);

        return ExpressOrderPrintCreateResult.builder().id(expressOrderPrint.getId()).build();
    }
}
