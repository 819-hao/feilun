package com.seeease.flywheel.serve.qt.event;

import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 通过或者异常
 * @Date create in 2023/3/13 14:21
 */
@Data
public class QtDecisionEvent implements BillHandlerEvent {

    private Integer stockId;

    private QualityTestingStateEnum qtState;

    private String originSerialNo;

    /**
     * 预计维修价
     */
    private BigDecimal fixMoney;

    /**
     * 维修id
     */
    private Integer fixId;

    /**
     * 质检来源
     */
    private BusinessBillTypeEnum businessBillTypeEnum;

    /**
     * 质检id
     */
    private Integer qtId;

    private Boolean returnOrChange;

    public QtDecisionEvent(Integer stockId, QualityTestingStateEnum qtState, String originSerialNo
            , BigDecimal fixMoney, Integer fixId, BusinessBillTypeEnum businessBillTypeEnum, Integer qtId,
                           Boolean returnOrChange) {
        this.stockId = stockId;
        this.qtState = qtState;
        this.originSerialNo = originSerialNo;
        this.fixMoney = fixMoney;
        this.fixId = fixId;
        this.businessBillTypeEnum = businessBillTypeEnum;
        this.qtId = qtId;
        this.returnOrChange = returnOrChange;
    }
}
