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
 * @Description 维修详情
 * @Date create in 2023/1/18 14:43
 */
@Data
public class FixDetailsResult implements Serializable {

    private Integer id;

    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    /**
     * 维修状态
     */
    private Integer fixState;


    /**
     * 维修来源
     */
    private Integer fixSource;


    /**
     * 流转等级
     */
    private Integer flowGrade;


    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    private Integer customerId;
    /**
     * 客户名称
     */
    /**
     * 客户联系人
     */
    private Integer customerContactId;

    private String customerCustomerName;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 开户银行
     */
    private String bank;

    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 是否返修
     */
    private Integer repairFlag;


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
     * 表的信息
     */
    private Integer stockId;

    private String stockSn;

    private BigDecimal pricePub;

    private String attachment;

    /**
     * 表带号
     */
    private String strap;

    private String taskArriveTime;

    /**
     * 维修时间
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

    private Integer fixType;

    private Integer maintenanceMasterId;

    private String maintenanceMasterName;

    private Integer defectOrNot;

    private String defectDescription;

    private Integer specialExpediting;

    private List<FixProjectMapper> content;


    private List<FixProjectResult> projectList;


    //
    /**
     * 实际维修时间
     */
    private Integer realityFixTime;

    /**
     * 质检维修时间
     */
    private Integer qtFixDay;

    /**
     * 预计维修完成时间
     */
    private String finishTime;

    private String timeoutMsg;

    /**
     * 实际完成时间
     */
    private String realityFinishTime;

    private Boolean timeout;

    //新字段

    private String parentFixSerialNo;


    /**
     * 维修日志
     */
    private List<FixLog> logList;

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
     * 订单类型
     */
    private Integer orderType;
    /**
     * 送外还是送内
     */
    private Integer tagType;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 客户诉求
     */
    private String customerDemand;

    /**
     * 完成类型0 取消 1 维修完成
     */
    private Integer finishType;

    /**
     * 配件结算总价
     */
    private BigDecimal attachmentCostPrice;

    /**
     * 维修结果项
     */
    private List<Integer> resultContent;

    /**
     * 维修结果维修单图片(维修结果)
     */
    private List<String> resultImgList;

    /**
     * 新建维修单图片(维修结果)
     */
    private List<String> newImgList;

    /**
     * 发货快递单号
     */
    private String deliverExpressNo;

    /**
     * 收货快递单号
     */
    private String deliveryExpressNo;

    /**
     * 配件列表
     */
    private List<AttachmentMapper> attachmentList;

    /**
     * 配件字符串
     */
    private String attachmentContent;

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
     * 维修项目名称 字符串
     */
    private String projectContent;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney;
    }

    /**
     * 能否送外维修
     */
    private Boolean tag;

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;


    /**
     * 接修人
     */
    private String repairName;

    /**
     * 接修时间
     */
    private String repairTime;

    /**
     * 分配时间
     */
    private String allotTime;

    /**
     * 返回备注
     */
    private String returnRemark;

    /**
     * 送修备注
     */
    private String fixRemark;

}
