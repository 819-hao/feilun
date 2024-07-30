package com.seeease.flywheel.serve.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.customer.request.CustomerAndContactsPageQueryRequest;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.customer.request.CustomerQueryRequest;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.customer.result.CustomerAndContractsPageQueryResult;
import com.seeease.flywheel.customer.result.CustomerPageResult;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【customer_contacts(供应商联系人
 * )】的数据库操作Service
 * @createDate 2023-01-31 17:02:20
 */
public interface CustomerContactsService extends IService<CustomerContacts> {

    Integer create(CustomerCreateRequest request, int customerId);

    void update(CustomerUpdateRequest request);

    Page<CustomerPageResult> query(CustomerQueryRequest request);

    List<CustomerContacts> searchByName(String name);

    List<CustomerContacts> searchByNameOrPhone(String customerName, String customerPhone);

    CustomerContacts queryCustomerContactsByNameAndPhone(String name, String phone);

    Page<CustomerAndContractsPageQueryResult> customerAndContractPageQry(CustomerAndContactsPageQueryRequest request);

    List<CustomerContacts> searchByCustomerId(Integer customerId);


    CustomerContacts  queryCustemerContactByCustomerId(int customerContactId);
}
