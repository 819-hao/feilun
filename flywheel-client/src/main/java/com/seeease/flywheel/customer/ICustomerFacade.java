package com.seeease.flywheel.customer;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.entity.CustomerInfo;
import com.seeease.flywheel.customer.request.*;
import com.seeease.flywheel.customer.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/3/1
 */
public interface ICustomerFacade {


    CustomerCreateResult create(CustomerCreateRequest request);

    void update(CustomerUpdateRequest request);

    PageResult<CustomerPageResult> query(CustomerQueryRequest request);

    /**
     * 企业供应商
     *
     * @param request
     * @return
     */
    PageResult<CustomerPageQueryResult> query(CustomerPageQueryRequest request);

    /**
     * 供应商联系人
     *
     * @param request
     * @return
     */
    PageResult<ContactsPageQueryResult> query(ContactsPageQueryRequest request);

    /**
     * 根据供应商名称查询
     *
     * @param customerNameList
     * @return
     */
    List<CustomerInfo> findByCustomerName(List<String> customerNameList);

    /**
     * 企业微信-新建确认收款，查询客户信息
     *
     * @param request
     * @return
     */
    PageResult<CustomerAndContractsPageQueryResult> customerAndContractsPageQry(CustomerAndContactsPageQueryRequest request);

}
