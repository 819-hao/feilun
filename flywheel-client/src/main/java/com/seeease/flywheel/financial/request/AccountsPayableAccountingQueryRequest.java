package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class AccountsPayableAccountingQueryRequest extends PageRequest {

    /**
     * 创建开始时间
     */
    private String createdStartTime;
    /**
     * 创建结束时间
     */
    private String createdEndTime;
    /**
     * 审核开始时间
     */
    private String auditStartTime;
    /**
     * 审核结束时间
     */
    private String auditEndTime;
    /**
     * 订单种类
     */
    private Integer type;
    private List<Integer> typeList;
    /**
     * 订单类型
     */
    private Integer classification;
    /**
     * 订单分类
     */
    private Integer originType;

    /**
     *
     */
    private Integer salesMethod;
    private Integer status;

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
     * 关联单号
     */
    private String originSerialNo;
    /**
     *
     */
    private String model;
    /**
     * 创建人
     */
    private String applicant;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;

    /**
     * 订单来源
     */
    private Integer shopId;
    /**
     * 客户ids
     */
    private List<Integer> customerIds;

    /**
     * 联系人ids
     */
    private List<Integer> contactsIds;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 实际采购人
     */
    private String purchaseBy;
    private List<Long> purchaseIds;
    private UseScenario useScenario;
    public enum UseScenario {

        /**
         * 应收
         */
        AMOUNT_RECEIVABLE,
        /**
         * 应付
         */
        AMOUNT_PAYABLE,
    }
}
