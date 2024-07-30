package com.seeease.flywheel.recycle.request;

import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商城回购创建采购单
 * @Auther Gilbert
 * @Date 2023/9/4 16:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class ReplacementOrRecycleCreateRequest implements Serializable {

    //主键id
    private Integer id;
    /**
     * 单号
     */
    private String serial;
    /**
     * 客户经理名称
     */
    private String employeeName;
    /**
     *客户经理id
     */
    private Integer employeeId;
    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户电话
     */
    private String customerPhone;
    //供应商id
    private Integer customerId;
    /**
     * 客户id
     */
    private Integer customerContactId;

    //类型用来区分来源：回收还是置换
    private Integer type;

    //用来区分大类：回收还是回购
    private Integer recycleType;
    /**
     * 需求门店名称
     */
    private String demandName;

    /**
     * 需求门店id
     */
    private Integer demandId;
    /**
     * 采购主体id
     */
    private Integer purchaseSubjectId;
    /**
     * 采购备注
     */
    private String remarks;
    /**
     * 仅回收单据详情
     */
    private List<PurchaseCreateRequest.BillPurchaseLineDto> billPurchaseLineDtoList;

    /**
     * 置换行数据
     */
    private List<ReplacementLineRequest> replacementLineDtoList;

    /**
     * 申请打款信息
     */
    private ApplyFinancialPaymentDetailResult applyFinancialPaymentDetailResult;

    private String applyPaymentSerialNo;
}
