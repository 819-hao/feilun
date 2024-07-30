package com.seeease.flywheel.fix.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 维修完成
 * @Date create in 2023/1/17 17:25
 */
@Data
public class FixFinishResult implements Serializable {

    private Integer id;

    /**
     * 维修单号
     */
    private String serialNo;

    /**
     * 维修商品
     */
    private Integer stockId;

    private Integer shopId;

    private Integer fixSource;

    /**
     * 附件成本总价
     */
    private BigDecimal attachmentCostPrice;

    //完成时，执行下一步操作 工作流参数

    private String parentFixSerialNo;

}
