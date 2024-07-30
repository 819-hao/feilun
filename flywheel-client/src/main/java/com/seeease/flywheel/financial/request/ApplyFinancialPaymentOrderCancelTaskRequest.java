package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class ApplyFinancialPaymentOrderCancelTaskRequest extends PageRequest {

    private Integer id;

    /**
     * 场景值 1 业务 2 财务
     */
    private Integer sceneType;

}
