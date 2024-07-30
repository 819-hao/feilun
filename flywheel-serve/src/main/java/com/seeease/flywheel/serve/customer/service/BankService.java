package com.seeease.flywheel.serve.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.customer.request.BankCreateRequest;
import com.seeease.flywheel.customer.request.BankQueryRequest;
import com.seeease.flywheel.customer.request.BankUpdateRequest;
import com.seeease.flywheel.customer.result.BankCreateResult;
import com.seeease.flywheel.customer.result.BankPageResult;
import com.seeease.flywheel.serve.customer.entity.Bank;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author edy
* @description 针对表【bank】的数据库操作Service
* @createDate 2023-03-01 13:47:38
*/
public interface BankService extends IService<Bank> {

    BankCreateResult create(BankCreateRequest request);

    void update(BankUpdateRequest request);

    Page<BankPageResult> query(BankQueryRequest request);
}
