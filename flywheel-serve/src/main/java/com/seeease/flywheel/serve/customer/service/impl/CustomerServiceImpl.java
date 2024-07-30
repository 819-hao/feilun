package com.seeease.flywheel.serve.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.serve.customer.convert.CustomerConvert;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.mapper.CustomerMapper;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【customer(供应商表)】的数据库操作Service实现
 * @createDate 2023-01-31 17:02:14
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>
        implements CustomerService {

    @Override
    public CustomerPO queryCustomerPO(Integer customerId) {
        return this.baseMapper.queryCustomerPO(customerId);
    }


    /**
     * @param customerIdList
     * @return
     */
    @Override
    public List<Customer> findCustomer(List<Integer> customerIdList) {
        if (CollectionUtils.isEmpty(customerIdList)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(Wrappers.<Customer>lambdaQuery()
                .in(Customer::getId, customerIdList));

    }

    @Override
    public List<Customer> searchByName(String customerName) {
        if (StringUtils.isEmpty(customerName)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(Wrappers.<Customer>lambdaQuery()
                .like(Customer::getCustomerName, customerName + "%"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(CustomerCreateRequest request) {
        Customer customer = CustomerConvert.INSTANCE.convert(request);
        this.baseMapper.insert(customer);
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerUpdateRequest request) {
        if (request.getCustomerId() == null)
            return;
        Customer customer = CustomerConvert.INSTANCE.convert(request);
        customer.setId(request.getCustomerId());
        this.baseMapper.updateById(customer);
    }

    @Override
    public List<Customer> searchByNameOrPhone(String customerName, String customerPhone) {
        if (StringUtils.isEmpty(customerName) && StringUtils.isEmpty(customerPhone))
            return Collections.EMPTY_LIST;
        return this.baseMapper.searchByNameOrPhone(customerName, customerPhone);
    }

    @Override
    public Customer queryCustomerById(@NonNull  Integer customerId) {
        LambdaQueryWrapper<Customer> customerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        customerLambdaQueryWrapper.eq(Customer::getId,customerId);
        return baseMapper.selectOne(customerLambdaQueryWrapper);
    }
}




