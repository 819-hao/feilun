package com.seeease.flywheel.serve.customer.rpc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.IBankFacade;
import com.seeease.flywheel.customer.request.*;
import com.seeease.flywheel.customer.result.BankCreateResult;
import com.seeease.flywheel.customer.result.BankPageResult;
import com.seeease.flywheel.serve.customer.service.BankService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author wbh
 * @date 2023/3/1
 */
@DubboService(version = "1.0.0")
public class BankFacade implements IBankFacade {

    @Resource
    private BankService bankService;

    @Override
    public BankCreateResult create(BankCreateRequest request) {
        return bankService.create(request);
    }

    @Override
    public void update(BankUpdateRequest request) {
        bankService.update(request);
    }

    @Override
    public PageResult<BankPageResult> query(BankQueryRequest request) {
        Page<BankPageResult> page = bankService.query(request);
        return PageResult.<BankPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }
}
