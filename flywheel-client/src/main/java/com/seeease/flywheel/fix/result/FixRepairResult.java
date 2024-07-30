package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/13 14:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixRepairResult implements Serializable {

    private Integer id;

    /**
     * 维修单号
     */
    private String serialNo;

    /**
     * 维修商品
     */
    private Integer stockId;

    private Integer fixSource;

    private Integer shopId;

    //
    /**
     * 是否分配
     */
    private Integer isAllot;

    /**
     * 是否接受
     */
    private Integer isAccept;

    /**
     * 附件成本总价
     */
    private BigDecimal attachmentCostPrice;

    //完成时，执行下一步操作 工作流参数

    private String parentFixSerialNo;
}
