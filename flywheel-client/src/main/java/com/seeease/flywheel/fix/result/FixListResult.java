package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class FixListResult implements Serializable {

    private Integer id;

    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * 维修来源
     */
    private Integer fixSource;

    /**
     * 是否返修
     */
    private Integer repairFlag;

    /**
     * 是否加急
     */
    private Integer specialExpediting;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 维修状态
     */
    private Integer fixState;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 客户名称
     */
    private String customerCustomerName;

    /**
     * 创建时间
     */
    private String createdTime;

    private String taskArriveTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 维修时间(时间)
     */
    private Integer fixDay;

    /**
     * 维修费用
     */
    private BigDecimal fixMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;

    /**
     * 备注
     */
    private String remark;
    /**
     * 接修时间
     */
    private String repairTime;

    /**
     * 实际维修时间
     */
    private Integer realityFixTime;

    /**
     * 质检维修时间
     */
    private Integer qtFixDay;

    /**
     * 预计维修完成时间(...)
     */
    private String finishTime;

    /**
     * 超时原因
     */
    private String timeoutMsg;

    private String maintenanceMasterName;

    private Integer maintenanceMasterId;

    private Integer shopId;

    private Integer stockId;

    // 新需求字段

    private Integer storeId;

    /**
     * 维修单归属
     */
    private String storeName;

    /**
     * 维修站点id
     */
    private Integer fixSiteId;

    /**
     * 维修站点名称
     */
    private String fixSiteName;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户联系电话
     */
    private String customerPhone;

    /**
     * 客户联系地址
     */
    private String customerAddress;

    /**
     * 送外还是送内
     */
    private Integer tagType;

    private List<FixProjectMapper> content;

    private String projectContent;

    /**
     * 维修完成 1
     * 维修取消 0
     */
    private Integer finishType;

    private Integer orderType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney;
    }


    private List<FixProjectResult> projectList;

    /**
     * 配件列表
     */
    private List<AttachmentMapper> attachmentList;

//    /**
//     * 配件字符串
//     */
//    private String attachmentContent;

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;

    /**
     * 配件集合
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentMapper implements Serializable {

        private Integer stockId;

        private String stockSn;

        private BigDecimal costPrice;

        private String model;

        private Integer goodsId;

        /**
         * 品牌
         */
        private String brandName;
        /**
         * 系列
         */
        private String seriesName;

        /**
         * 主图
         */
        private String image;
    }

    /**
     * 维修结果项
     */
    private List<Integer> resultContent;

    /**
     * 维修结果维修单图片(维修结果)
     */
    private List<String> resultImgList;

    /**
     * 维修加急/返修
     */
    private String tag;

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 返回备注
     */
    private String returnRemark;

    /**
     * 送修备注
     */
    private String fixRemark;
}
