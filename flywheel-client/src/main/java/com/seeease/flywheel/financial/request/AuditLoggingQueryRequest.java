package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class AuditLoggingQueryRequest extends PageRequest {

    /**
     * 创建开始时间
     */
    private String createdStartTime;
    /**
     * 创建结束时间
     */
    private String createdEndTime;
    /**
     * 订单类型
     */
    private Integer type;
    /**
     * 分类
     */
    private Integer classification;
    /**
     *
     */
    private Integer salesMethod;
    /**
     * 订单来源
     */
    private Integer shopId;

    /**
     * 客户类别
     */
    private Integer customerType;

    /**
     * 搜索客户条件
     */
    private String searchCustomerCriteria;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 申请打款编号
     */
    private String afpSerialNo;
    /**
     * 确认收款单编号
     */
    private String arcSerialNo;
    /**
     *
     */
    private String model;
    /**
     * 创建人
     */
    private String applicant;
    /**
     * 客户ids
     */
    private List<Integer> customerIds;

    /**
     * 联系人ids
     */
    private List<Integer> contactsIds;
}
