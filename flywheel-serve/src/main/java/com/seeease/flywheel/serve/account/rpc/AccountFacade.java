package com.seeease.flywheel.serve.account.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.account.IAccountFacade;
import com.seeease.flywheel.account.request.*;
import com.seeease.flywheel.account.result.AccountCreateResult;
import com.seeease.flywheel.account.result.AccountQueryImportResult;
import com.seeease.flywheel.account.result.AccountQueryResult;
import com.seeease.flywheel.serve.account.convert.AccountConverter;
import com.seeease.flywheel.serve.account.entity.KeepAccount;
import com.seeease.flywheel.serve.account.enums.PageTypeEnum;
import com.seeease.flywheel.serve.account.service.KeepAccountService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:30
 */
@DubboService(version = "1.0.0")
public class AccountFacade implements IAccountFacade {

    @Resource
    private KeepAccountService service;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AccountCreateResult> batchCreate(List<AccountCreateRequest> request) {

        service.saveBatch(request.stream().map(accountCreateRequest -> AccountConverter.INSTANCE.convert(accountCreateRequest)).collect(Collectors.toList()));

        return Arrays.asList();
    }

    @Override
    public PageResult<AccountQueryResult> list(AccountQueryRequest request) {

        LambdaQueryWrapper<KeepAccount> query = Wrappers.<KeepAccount>lambdaQuery();

        if (StringUtils.isNotBlank(request.getCompleteDateStart()) && StringUtils.isNotBlank(request.getCompleteDateEnd())) {
            query.between(KeepAccount::getCompleteDate, StringUtils.substring(request.getCompleteDateStart(), 0, 10), StringUtils.substring(request.getCompleteDateEnd(), 0, 10));
        }

        if (ObjectUtils.isNotEmpty(request.getCompanyName())) {
            query.eq(KeepAccount::getCompanyName, request.getCompanyName());
        }

        if (ObjectUtils.isNotEmpty(request.getShopName())) {
            query.eq(KeepAccount::getShopName, request.getShopName());
        }

        if (ObjectUtils.isNotEmpty(request.getAccountGroup())) {
            query.eq(KeepAccount::getAccountGroup, request.getAccountGroup());
        }

        if (ObjectUtils.isNotEmpty(request.getAccountType())) {
            query.eq(KeepAccount::getAccountType, request.getAccountType());
        }

        query.eq(KeepAccount::getPageType, PageTypeEnum.fromCode(request.getPageType()));

        Page<KeepAccount> page = service.page(new Page<>(request.getPage(), request.getLimit()), query);

        if (0L == page.getTotal()) {
            return PageResult.<AccountQueryResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }

        return PageResult.<AccountQueryResult>builder()
                .result(page.getRecords()
                        .stream()
                        .map(t -> AccountConverter.INSTANCE.convertAccountQueryResult(t))
                        .collect(Collectors.toList())
                )
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult<AccountQueryImportResult> queryImport(AccountImportRequest request) {

        List<Integer> list = new ArrayList<>();

        if (request instanceof AccountImportByFinanceQueryRequest) {
            AccountImportByFinanceQueryRequest accountImportByFinanceQueryRequest = (AccountImportByFinanceQueryRequest) request;
            for (AccountImportByFinanceQueryRequest.ImportDto importDto : accountImportByFinanceQueryRequest.getDataList()) {
                KeepAccount keepAccount = AccountConverter.INSTANCE.convert(importDto);
                keepAccount.setCompleteDate(Optional.ofNullable(importDto.getCompleteDate()).map(r -> DateFormatUtils.format(r, "yyyy-MM-dd")).orElse(null));
                keepAccount.setPageType(PageTypeEnum.fromCode(request.getPageType()));
                service.save(keepAccount);
                list.add(keepAccount.getId());
            }

        } else if (request instanceof AccountImportByManpowerQueryRequest) {
            AccountImportByManpowerQueryRequest accountImportByFinanceQueryRequest = (AccountImportByManpowerQueryRequest) request;
            for (AccountImportByManpowerQueryRequest.ImportDto importDto : accountImportByFinanceQueryRequest.getDataList()) {
                KeepAccount keepAccount = AccountConverter.INSTANCE.convert(importDto);
                keepAccount.setCompleteDate(Optional.ofNullable(importDto.getCompleteDate()).map(r -> DateFormatUtils.format(r, "yyyy-MM-dd")).orElse(null));
                keepAccount.setPageType(PageTypeEnum.fromCode(request.getPageType()));
                service.save(keepAccount);
                list.add(keepAccount.getId());
            }
        } else if (request instanceof AccountImportByPeopleQueryRequest) {
            AccountImportByPeopleQueryRequest accountImportByFinanceQueryRequest = (AccountImportByPeopleQueryRequest) request;
            for (AccountImportByPeopleQueryRequest.ImportDto importDto : accountImportByFinanceQueryRequest.getDataList()) {
                KeepAccount keepAccount = AccountConverter.INSTANCE.convert(importDto);
                keepAccount.setCompleteDate(Optional.ofNullable(importDto.getCompleteDate()).map(r -> DateFormatUtils.format(r, "yyyy-MM-dd")).orElse(null));
                keepAccount.setPageType(PageTypeEnum.fromCode(request.getPageType()));
                service.save(keepAccount);
                list.add(keepAccount.getId());
            }
        }
        return ImportResult.<AccountQueryImportResult>builder()
                .successList(list.stream().map(i -> AccountQueryImportResult.builder().id(i).build()).collect(Collectors.toList()))
                .errList(Arrays.asList())
                .build();
    }

    @Override
    public void delete(AccountImportRequest request) {
        LambdaQueryWrapper<KeepAccount> query = Wrappers.<KeepAccount>lambdaQuery()
                .eq(KeepAccount::getPageType, request.getPageType());

        if (request instanceof AccountDeleteByFinanceRequest) {
            AccountDeleteByFinanceRequest accountDeleteByFinanceRequest = (AccountDeleteByFinanceRequest) request;
            query
                    .between(KeepAccount::getCompleteDate, accountDeleteByFinanceRequest.getCompleteDateStart(), accountDeleteByFinanceRequest.getCompleteDateEnd())
//                    .in(KeepAccount::getAccountGroup, accountDeleteByFinanceRequest.getAccountGroupList())
            ;
            service.remove(query);
        }
    }

    @Override
    public void delete(AccountDeleteRequest request) {
        service.removeBatchByIds(request.getList());
    }
}
