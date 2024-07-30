package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修创建
 * @Date create in 2023/1/17 17:25
 */
@Data
public class FixCreateRequest implements Serializable {

    //*********** 老需求 start******************

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 仓库预作业单
     */
    private String storeWorkSerialNo;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 维修来源
     */
    private Integer fixSource;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人id
     */
    private Integer customerContactId;

    /**
     * 维修建议
     */
    private String fixAdvise;

    //*********** 老需求 end******************


    //************* PVOJ-151 start*******************

    /**
     * 品牌id
     */
    private Integer brandId;

    private String brandName;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 父级维修id
     */
    private Integer parentFixId;

    /**
     * 维修站点id
     */
    private Integer fixSiteId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户手机
     */
    private String customerPhone;
    /**
     * 客户地址
     */
    private String customerAddress;

    /**
     * 附件消耗总价
     */
    private BigDecimal attachmentCostPrice;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 新建图片链接列表
     */
    private List<String> newImgList;

    /**
     * 标记维修审核类型
     */
    private Integer tagType;

    /**
     * 发货快递单号
     */
    private String deliverExpressNo;

    /**
     * 收货快递单号
     */
    private String deliveryExpressNo;

    /**
     * 维修结果项
     */
    private List<Integer> resultContent;

    /**
     * 维修结果项图片
     */
    private List<String> resultImgList;

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
     * 完成类型
     */
    private Integer finishType;

    /**
     * '建单门店id（所属门店）'
     */
    private Integer storeId;

    /**
     * '来源门店id'
     */
    private Integer parentStoreId;

    /**
     * 维修师
     */
    private Integer maintenanceMasterId;

    /**
     * 流转等级
     */
    private Integer flowGrade;


    /**
     * 维修项
     */
    private List<FixCreateRequest.FixProjectMapper> content;

    /**
     * 附件id list
     */
    private List<Integer> attachmentIdList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixProjectMapper implements Serializable {

        private Integer fixProjectId;

        private BigDecimal fixMoney;
    }

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 维修时间
     */
    private Integer fixDay;

    //************* PVOJ-151 end*******************

    //异步创建，送检新建单据 storeId

    //工作流参数

    /**
     * 门店简码
     */
//    private String shortcodes;

    /**
     * 总部字段
     */
//    private Integer storeId;


    /**
     * 是否接修
     */
//    private Integer isRepair;
//
//    /**
//     * 是否分配
//     */
//    private Integer isAllot;

    //工作流参数

    /**
     * 创建人id
     */
    private Integer createdId;

    /**
     * 创建人
     */
    private String createdBy;

}
