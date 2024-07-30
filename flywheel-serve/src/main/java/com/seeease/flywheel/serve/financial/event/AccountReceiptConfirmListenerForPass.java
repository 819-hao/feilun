package com.seeease.flywheel.serve.financial.event;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.CollectionTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


/**
 * 确认收款
 */
@Component
public class AccountReceiptConfirmListenerForPass implements BillHandlerEventListener<AccountReceiptConfirmPassEvent> {

    @Resource
    private AccountsPayableAccountingService accountingService;
    private static final List<Integer> TYPE_ENUMS = Lists.newArrayList(
            CollectionTypeEnum.XF_TK.getValue(),
            CollectionTypeEnum.CG_TK.getValue());

    @Override
    public void onApplicationEvent(AccountReceiptConfirmPassEvent event) {

        if (StringUtils.isNotEmpty(event.getOriginSerialNo()) && TYPE_ENUMS.contains(event.getTypePayment())) {

            LambdaQueryWrapper<AccountsPayableAccounting> wrapper = new LambdaQueryWrapper<AccountsPayableAccounting>()
                    .eq(AccountsPayableAccounting::getOriginSerialNo, event.getOriginSerialNo())
                    .notIn(AccountsPayableAccounting::getStatus, Collections.singletonList(FinancialStatusEnum.AUDITED));

            if (Objects.nonNull(event.getStockIdList()) && CollectionUtils.isNotEmpty(event.getStockIdList())) {
                wrapper.in(AccountsPayableAccounting::getStockId, event.getStockIdList());
            }


            accountingService.list(wrapper)
                    .forEach(a -> {
                        AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                        accounting.setId(a.getId());
                        accounting.setArcSerialNo(event.getArcSerialNo());
                        accounting.setAuditor(UserContext.getUser().getUserName());
                        accounting.setWaitAuditPrice(event.getWaitAuditPrice());
                        accounting.setStatus(event.getStatusEnum());
                        accounting.setAuditTime(new Date());
                        accounting.setAuditDescription(FinancialStatusEnum.AUDITED.equals(event.getStatusEnum()) ? "确认收款核销(自动)" : "确认收款部分核销");
                        accountingService.updateById(accounting);
                    });
        }
    }
}
