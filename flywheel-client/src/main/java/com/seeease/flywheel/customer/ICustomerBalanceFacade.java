package com.seeease.flywheel.customer;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.request.CustomerBalanceRefundRequest;
import com.seeease.flywheel.customer.result.CustomerBalanceDetailResult;
import com.seeease.flywheel.customer.result.CustomerBalancePageResult;
import com.seeease.springframework.Response;

import java.util.List;

public interface ICustomerBalanceFacade {

    /**
     * 客户余额查询
     *
     * @return
     */
    PageResult<CustomerBalancePageResult> customerBalancePageQuery(CustomerBalancePageRequest request);

    /**
     * 客户余额退款
     *
     * @return
     */
    void customerBalanceRefund(CustomerBalanceRefundRequest request);

    /**
     * 查看客户余额详情
     *
     * @param customerId
     * @return
     */
    List<CustomerBalanceDetailResult> queryAccountBalanceDetail(Integer customerId);

    /**
     * 查看寄售保证金详情
     *
     * @param customerId
     * @return
     */
    List<CustomerBalanceDetailResult> queryconsignmentMarginDetail(Integer customerId);

}
