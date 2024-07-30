package com.seeease.flywheel.customer;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.request.*;
import com.seeease.flywheel.customer.result.BankCreateResult;
import com.seeease.flywheel.customer.result.BankPageResult;

/**
 * @author wbh
 * @date 2023/3/1
 */
public interface IBankFacade {


    BankCreateResult create(BankCreateRequest request);

    void update(BankUpdateRequest request);

    PageResult<BankPageResult> query(BankQueryRequest request);
}
