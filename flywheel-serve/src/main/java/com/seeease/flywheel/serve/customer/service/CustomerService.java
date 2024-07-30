package com.seeease.flywheel.serve.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import lombok.NonNull;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【customer(供应商表)】的数据库操作Service
 * @createDate 2023-01-31 17:02:14
 */
public interface CustomerService extends IService<Customer> {

    CustomerPO queryCustomerPO(Integer customerId);

    /**
     * 根据客户id批量查询客户
     *
     * @param customerIdList
     * @return
     */
    List<Customer> findCustomer(List<Integer> customerIdList);

    /**
     * 根据名称搜索客户
     *
     * @param customerName
     * @return
     */
    List<Customer> searchByName(String customerName);

    int create(CustomerCreateRequest request);

    void update(CustomerUpdateRequest request);

    List<Customer> searchByNameOrPhone(String customerName, String customerPhone);

    Customer queryCustomerById(@NonNull Integer customerId);
}
