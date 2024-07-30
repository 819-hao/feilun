package com.seeease.flywheel.serve.financial.rpc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IAuditLoggingFacade;
import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.flywheel.financial.result.AuditLoggingDetailResult;
import com.seeease.flywheel.financial.result.AuditLoggingPageResult;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.service.AuditLoggingDetailService;
import com.seeease.flywheel.serve.financial.service.AuditLoggingService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/5/11
 */
@DubboService(version = "1.0.0")
public class AuditLoggingFacade implements IAuditLoggingFacade {
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService contactsService;
    @Resource
    private AuditLoggingService auditLoggingService;
    @Resource
    private AuditLoggingDetailService detailService;
    @Resource
    private PurchaseSubjectService subjectService;
    @Resource
    private StoreManagementService storeManagementService;

    @Override
    public PageResult<AuditLoggingPageResult> query(AuditLoggingQueryRequest request) {
        if (StringUtils.isNotEmpty(request.getSearchCustomerCriteria())) {
            List<Integer> customerIds = customerService.searchByNameOrPhone(request.getSearchCustomerCriteria(), null)
                    .stream().map(Customer::getId).collect(Collectors.toList());
            List<Integer> contactsIds = contactsService.searchByName(request.getSearchCustomerCriteria())
                    .stream().map(CustomerContacts::getId).collect(Collectors.toList());
            request.setContactsIds(contactsIds.size() > 0 ? contactsIds : Lists.newArrayList(-1));
            request.setCustomerIds(customerIds.size() > 0 ? customerIds : Lists.newArrayList(-1));
        }
        Page<AuditLoggingPageResult> page = auditLoggingService.getPage(request);
        List<AuditLoggingPageResult> list = page.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.<AuditLoggingPageResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }

        Map<Integer, String> customerMap = Optional.ofNullable(list.stream()
                        .map(AuditLoggingPageResult::getCustomerId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(customerService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(Customer::getId, Customer::getCustomerName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        Map<Integer, String> contactsMap = Optional.ofNullable(list.stream()
                        .map(AuditLoggingPageResult::getCustomerContactId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(contactsService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(CustomerContacts::getId, CustomerContacts::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        Map<Integer, String> shopMap = Optional.ofNullable(list.stream()
                        .map(AuditLoggingPageResult::getShopId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(storeManagementService::selectInfoByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        list.forEach(r -> {
            r.setCustomerName(customerMap.getOrDefault(r.getCustomerId(), "未知企业客户"));
            r.setCustomerContactName(contactsMap.getOrDefault(r.getCustomerContactId(), "未知个人客户"));
            r.setShopName(shopMap.getOrDefault(r.getShopId(), "未知来源"));
        });
        return PageResult.<AuditLoggingPageResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<AuditLoggingDetailResult> detail(AuditLoggingDetailRequest request) {
        List<AuditLoggingDetailResult> list = detailService.getOneByRequest(request);
        if (CollectionUtils.isEmpty(list))
            return Lists.newArrayList();
        Map<Integer, String> subjectMap = Optional.ofNullable(list.stream()
                        .map(AuditLoggingDetailResult::getBelongId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(subjectService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        list.forEach(r -> {
            r.setBelongName(subjectMap.getOrDefault(r.getBelongId(), "未知归属"));
        });
        return list;
    }
}
