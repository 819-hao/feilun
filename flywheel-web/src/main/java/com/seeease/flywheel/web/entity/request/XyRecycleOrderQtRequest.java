package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 质检
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderQtRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 型号
     */
    private String model;

    /**
     * 最终的估价价格，成交金额
     */
    private BigDecimal finalApprizeAmount;

    /**
     * 质检报告
     */
    private String qtReport;

    /**
     * 质检成色
     */
    private String qtFineness;

    /**
     * 质检编码
     */
    private String qtCode;

    /**
     * 质检外观检测
     */
    private List<String> qtFacade;

    /**
     * 质检细节检测
     */
    private List<String> qtDetail;

    /**
     * 质检附件
     */
    private List<String> qtAttachment;

}
