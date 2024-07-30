package com.seeease.flywheel.serve.helper.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditRequest;
import com.seeease.flywheel.helper.request.BusinessCustomerListRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;
import com.seeease.flywheel.serve.helper.convert.BusinessCustomerAuditConvert;
import com.seeease.flywheel.serve.helper.enmus.BusinessCustomerAuditStatusEnum;
import com.seeease.flywheel.serve.helper.mapper.AppBusinessCustomerAuditMapper;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import com.seeease.flywheel.serve.helper.service.BusinessCustomerAuditService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.springframework.stereotype.Service;

@Service
public class BusinessCustomerAuditServiceImpl extends ServiceImpl<AppBusinessCustomerAuditMapper, BusinessCustomerAudit> implements BusinessCustomerAuditService {

    @Override
    public Integer submit(BusinessCustomerAuditCreateRequest request) {
        BusinessCustomerAudit businessCustomerAudit = BusinessCustomerAuditConvert.INSTANCE.to(request);

        if (businessCustomerAudit.getId() != null) {
            BusinessCustomerAudit audit = getById(request.getId());
            if (null != audit) {
                if (audit.getStatus() == BusinessCustomerAuditStatusEnum.FAIL) {
                    businessCustomerAudit.setTransitionStateEnum(BusinessCustomerAuditStatusEnum.TransitionEnum.RETRY);
                    UpdateByIdCheckState.update(getBaseMapper(), businessCustomerAudit);
                } else if (audit.getStatus() == BusinessCustomerAuditStatusEnum.WAIT){
                    getBaseMapper().updateById(businessCustomerAudit);
                }
            }
        } else {
            businessCustomerAudit.setStatus(BusinessCustomerAuditStatusEnum.WAIT);
            getBaseMapper().insert(businessCustomerAudit);
        }
        return businessCustomerAudit.getId();

    }

    @Override
    public void audit(BusinessCustomerAuditRequest request) {
        BusinessCustomerAuditStatusEnum e = BusinessCustomerAuditStatusEnum.of(request.getStatus());
        BusinessCustomerAudit entity = BusinessCustomerAudit.builder()
                .id(request.getId())
                .transitionStateEnum(e == BusinessCustomerAuditStatusEnum.OK ?
                        BusinessCustomerAuditStatusEnum.TransitionEnum.OK :
                        BusinessCustomerAuditStatusEnum.TransitionEnum.FAIL)
                .build();
        UpdateByIdCheckState.update(getBaseMapper(), entity);
    }

    @Override
    public Page<BusinessCustomerPageResult> pageOf(BusinessCustomerListRequest request,
                                                   boolean admin,
                                                   Integer userid) {
        return getBaseMapper().pageOf(Page.of(request.getPage(),request.getLimit()),request,admin,userid);
    }
}
