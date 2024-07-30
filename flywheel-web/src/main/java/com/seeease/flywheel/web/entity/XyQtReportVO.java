package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/12/26
 */
@Data
public class XyQtReportVO implements Serializable {

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 型号
     */
    private String model;

    /**
     * 卖家图
     */
    private List<String> sellerImages;

    /**
     * 机芯类型
     */
    private String movementType;

    /**
     * 估价价格
     */
    private BigDecimal apprizeAmount;

    /**
     * 最终的估价价格，成交金额
     */
    private BigDecimal finalApprizeAmount;

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

    private String createdTime;
    /**
     * 质检报告
     */
    private String qtReport;

}
