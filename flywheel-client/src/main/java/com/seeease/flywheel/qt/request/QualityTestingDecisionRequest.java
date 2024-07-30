package com.seeease.flywheel.qt.request;

import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检完成判定
 * @Date create in 2023/1/17 15:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityTestingDecisionRequest implements Serializable {

    /**
     * 质检状态
     */
    private Integer qtState;

    private Integer qualityTestingId;

    /**
     * 异常id
     */
    private Integer exceptionReasonId;

    /**
     * 异常原因说明
     */
    private String exceptionReason;

    /**
     * 维修费用
     */
    private BigDecimal fixMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;


    private String returnReasonId;

    /**
     * 退货原因
     */
    private String returnReason;

    /**
     * 退货图片
     */
    private String returnImgs;

    /**
     * 检验数据 -- 插入数据
     */
    private QualityTestingDetailsResult qualityTestingDetailsResult;

    /**
     * 维修时间
     */
    private Integer fixDay;

    /**
     * 维修内容
     */
    private List<FixProjectMapper> content;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney = BigDecimal.ZERO;
    }

    /**
     * 原因
     */
    private String returnFixRemarks;

    /**
     * 表身号
     */
    private String returnNewStockSn;
}
