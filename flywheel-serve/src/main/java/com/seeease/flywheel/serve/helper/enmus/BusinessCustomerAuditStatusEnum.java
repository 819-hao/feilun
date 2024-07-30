package com.seeease.flywheel.serve.helper.enmus;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum BusinessCustomerAuditStatusEnum implements IStateEnum<Integer> {
    WAIT(1),OK(2),FAIL(3)
    ;
    private Integer value;


    public static BusinessCustomerAuditStatusEnum of (Integer value){
        return Arrays.stream(BusinessCustomerAuditStatusEnum.values()).filter(v-> v.getValue().equals(value)).findFirst().orElseThrow(() -> new IllegalArgumentException("错误的审核状态"));
    }


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        OK(BusinessCustomerAuditStatusEnum.WAIT, BusinessCustomerAuditStatusEnum.OK,"审核成功"),
        FAIL(BusinessCustomerAuditStatusEnum.WAIT, BusinessCustomerAuditStatusEnum.FAIL,"审核失败"),
        RETRY(BusinessCustomerAuditStatusEnum.FAIL,BusinessCustomerAuditStatusEnum.WAIT,"重新编辑"),
        ;

        private BusinessCustomerAuditStatusEnum fromState;
        private BusinessCustomerAuditStatusEnum toState;
        private String desc;


    }
}
