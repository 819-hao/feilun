package com.seeease.flywheel.web.common.work.consts;

import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/2 19:21
 */
@Getter
@AllArgsConstructor
public enum BusinessMappingProcessEnum implements Serializable {
    TH_CG_DJ(101, "同行采购-订金", ProcessDefinitionKeyEnum.PURCHASE),
    TH_CG_BH(102, "同行采购-备货", ProcessDefinitionKeyEnum.PURCHASE),
    TH_CG_PL(103, "同行采购-批量", ProcessDefinitionKeyEnum.PURCHASE),
    TH_JS(104, "同行寄售", ProcessDefinitionKeyEnum.PURCHASE),
    GR_JS(105, "个人寄售", ProcessDefinitionKeyEnum.PERSONAL_CONSIGN_SALE),
    GR_HS_JHS(106, "个人回收-仅回收", ProcessDefinitionKeyEnum.RECYCLE),
    GR_HS_ZH(107, "个人回收-置换", ProcessDefinitionKeyEnum.RECYCLE),
    GR_HG_JHS(108, "个人回购-回收", ProcessDefinitionKeyEnum.RECYCLE),
    GR_HG_ZH(109, "个人回购-置换", ProcessDefinitionKeyEnum.RECYCLE),
    TH_CG_QK(110, "同行采购-全款", ProcessDefinitionKeyEnum.PURCHASE),
    TH_CG_DJTP(111, "同行采购-定金特批", ProcessDefinitionKeyEnum.PURCHASE),

    ;
    /**
     * 业务单据类型
     */
    private Integer businessKey;

    private String name;
    /**
     * 流程定义的枚举
     */
    private ProcessDefinitionKeyEnum processKey;

    public static ProcessDefinitionKeyEnum fromValue(int value)  {
        return Arrays.stream(BusinessMappingProcessEnum.values())
                .filter(t -> value == t.getBusinessKey().intValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(new BusinessExceptionCode() {

                    @Override
                    public int getErrCode() {
                        return -1;
                    }

                    @Override
                    public String getErrMsg() {
                        return "没找到";
                    }
                })).getProcessKey();
    }

    public static BusinessMappingProcessEnum fromValue2(int value) {
        return Arrays.stream(BusinessMappingProcessEnum.values())
                .filter(t -> value == t.getBusinessKey().intValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(new BusinessExceptionCode() {

                    @Override
                    public int getErrCode() {
                        return -1;
                    }

                    @Override
                    public String getErrMsg() {
                        return "没找到";
                    }
                }));
    }
}
