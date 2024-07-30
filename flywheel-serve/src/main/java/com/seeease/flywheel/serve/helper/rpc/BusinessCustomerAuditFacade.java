package com.seeease.flywheel.serve.helper.rpc;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.IBusinessCustomerAuditFacade;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerListRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.ApplyFinancialPaymentNotice;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.helper.convert.BusinessCustomerAuditConvert;
import com.seeease.flywheel.serve.helper.enmus.BusinessCustomerAuditStatusEnum;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import com.seeease.flywheel.serve.helper.service.BusinessCustomerAuditService;

import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;


@Slf4j
@DubboService(version = "1.0.0")
public class BusinessCustomerAuditFacade implements IBusinessCustomerAuditFacade {
    @Resource
    private BusinessCustomerAuditService businessCustomerAuditService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;

    @Override
    public Integer submit(BusinessCustomerAuditCreateRequest request) {
       return businessCustomerAuditService.submit(request);
    }

    @Override
    public PageResult<BusinessCustomerPageResult> page(BusinessCustomerListRequest request) {
        boolean admin = UserContext.getUser().getRoles().stream().anyMatch(i->i.getRoleName().equals("admin"));


        Page<BusinessCustomerPageResult> pageRet =  businessCustomerAuditService.pageOf(
                request,
                admin,
                UserContext.getUser().getId()
        );
        pageRet.getRecords().forEach(i->{
            if (StringUtils.isNotEmpty(i.getAreaIdsJson())){
                i.setAreaIds(JSONArray.parseArray(i.getAreaIdsJson(),Integer.class));
            }
        });

        return PageResult.<BusinessCustomerPageResult>builder()
                .totalPage(pageRet.getPages())
                .totalCount(pageRet.getTotal())
                .result(pageRet.getRecords())
                .build();
    }

    @Override
    public void audit(BusinessCustomerAuditRequest request) {



        BusinessCustomerAuditStatusEnum e = BusinessCustomerAuditStatusEnum.of(request.getStatus());

        transactionTemplate.executeWithoutResult((t)->{
            businessCustomerAuditService.audit(request);
            if (e == BusinessCustomerAuditStatusEnum.OK){
                BusinessCustomerAudit audit = businessCustomerAuditService.getById(request.getId());
                if (audit != null){
                    //同步到企业客户表
                    LambdaQueryWrapper<Customer> eq = Wrappers.<Customer>lambdaQuery()
                            .eq(Customer::getDelFlag, 0)
                            .eq(Customer::getCustomerName, audit.getFirmName())
                            .eq(Customer::getType, CustomerTypeEnum.ENTERPRISE);
                    Customer customer = customerService.getOne(eq);
                    if (customer == null){
                        customer = new Customer();
                        customer.setType( CustomerTypeEnum.ENTERPRISE);
                        customer.setCustomerName(audit.getFirmName());
                        customer.setUpdateBy(audit.getCreatedBy());
                        customerService.save(customer);
                    }
                    CustomerContacts cc = customerContactsService.getOne(Wrappers.<CustomerContacts>lambdaQuery()
                            .eq(CustomerContacts::getPhone, audit.getContactPhone())
                            .eq(CustomerContacts::getCustomerId,customer.getId()));
                    if (cc == null){
                        cc = new CustomerContacts();
                    }
                    cc.setCustomerId(customer.getId());
                    cc.setName(audit.getContactName());
                    cc.setPhone(audit.getContactPhone());
                    cc.setAreaIds(audit.getAreaIds());
                    cc.setProp(audit.getProp());
                    cc.setAddress(audit.getContactArea() + "/" + audit.getContactAddress());
                    cc.setCnmaeCcnamePhone(audit.getFirmName() +"/"+customer.getCustomerName()+ "/" + cc.getAddress());
                    customerContactsService.saveOrUpdate(cc);
                }

            }
        });


    }
}
