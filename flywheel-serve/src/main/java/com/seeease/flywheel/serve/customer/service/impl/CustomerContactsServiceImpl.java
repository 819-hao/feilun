package com.seeease.flywheel.serve.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.customer.request.CustomerAndContactsPageQueryRequest;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.customer.request.CustomerQueryRequest;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.customer.result.CustomerAndContractsPageQueryResult;
import com.seeease.flywheel.customer.result.CustomerPageResult;
import com.seeease.flywheel.serve.customer.convert.CustomerConvert;
import com.seeease.flywheel.serve.customer.entity.Bank;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.mapper.BankMapper;
import com.seeease.flywheel.serve.customer.mapper.CustomerContactsMapper;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【customer_contacts(供应商联系人
 * )】的数据库操作Service实现
 * @createDate 2023-01-31 17:02:20
 */
@Service
public class CustomerContactsServiceImpl extends ServiceImpl<CustomerContactsMapper, CustomerContacts>
        implements CustomerContactsService {

    @Resource
    private BankMapper bankMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer create(CustomerCreateRequest request, int customerId) {
        CustomerContacts customerContacts = CustomerConvert.INSTANCE.convertContacts(request);
        customerContacts.setCustomerId(customerId);
        this.baseMapper.insert(customerContacts);
        return customerContacts.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerUpdateRequest request) {
        if (request.getCustomerContactsId() == null)
            return;
        CustomerContacts customerContacts = CustomerConvert.INSTANCE.convertContacts(request);
        customerContacts.setId(request.getCustomerContactsId());
        this.baseMapper.updateById(customerContacts);
    }

    @Override
    public Page<CustomerPageResult> query(CustomerQueryRequest request) {
        Page<CustomerPageResult> page = this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
        page.getRecords().forEach(customerPageResult -> {
            customerPageResult.setList(CustomerConvert.INSTANCE.convertContacts(bankMapper.selectList(new LambdaQueryWrapper<Bank>()
                    .eq(Bank::getCustomerId, customerPageResult.getCustomerId()))));
        });
        return page;
    }

    @Override
    public List<CustomerContacts> searchByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(Wrappers.<CustomerContacts>lambdaQuery()
                .like(CustomerContacts::getName, name));
    }

    @Override
    public List<CustomerContacts> searchByNameOrPhone(String customerName, String customerPhone) {
        if (StringUtils.isEmpty(customerName) && StringUtils.isEmpty(customerPhone))
            return Collections.EMPTY_LIST;
        LambdaQueryWrapper<CustomerContacts> lambdaQuery = Wrappers.<CustomerContacts>lambdaQuery();
        if (StringUtils.isNotEmpty(customerPhone))
            lambdaQuery.eq(CustomerContacts::getPhone, customerPhone);
        if (StringUtils.isNotEmpty(customerName))
            lambdaQuery.like(CustomerContacts::getName, customerName);
        return baseMapper.selectList(lambdaQuery);
    }

    @Override
    public CustomerContacts queryCustomerContactsByNameAndPhone(String name, String phone) {
        return baseMapper.selectOne(new LambdaQueryWrapper<CustomerContacts>()
                .eq(CustomerContacts::getPhone, phone)
                .eq(CustomerContacts::getName, name));
    }

    @Override
    public Page<CustomerAndContractsPageQueryResult> customerAndContractPageQry(CustomerAndContactsPageQueryRequest request) {
        Page<CustomerAndContractsPageQueryResult> page = this.baseMapper.
                customerAndContractsPage(new Page(request.getPage(), request.getLimit()), request);

        return page;
    }

    @Override
    public List<CustomerContacts> searchByCustomerId(Integer customerId) {
        if (null == customerId) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(Wrappers.<CustomerContacts>lambdaQuery()
                .like(CustomerContacts::getCustomerId, customerId));
    }


    @Override
    public CustomerContacts queryCustemerContactByCustomerId(int customerContactId) {
        LambdaQueryWrapper<CustomerContacts> queryWrapper = new LambdaQueryWrapper<CustomerContacts>()
                .eq(CustomerContacts::getId, customerContactId)
                .last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }
}




