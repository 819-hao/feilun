package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialPageAllResult implements Serializable {


    /**
     * $column.columnComment
     */
    private Integer id;

    /**
     * 订单号
     */
    private String serialNumber;

    /**
     * 关联单号
     */
    private String assocSerialNumber;

    /**
     * 三方订单
     */
    private String thirdNumber;

    /**
     * 订单类型CGRK(0),CGTH(1),XSCK(2),XSTH(3)
     */
    private Integer orderType;
    /**
     * SALE_NORMAL(1, "正常"),
     * SALE_DEPOSIT(2, "订金"),
     * SALE_PRESENTED(3, "赠送"),
     * SALE_CONSIGN_FOR_SALE(4, "寄售"),
     * SALE_ON_LINE(5, "平台"),
     * PURCHASE_DEPOSIT(6,"定金"),
     * PURCHASE_PREPARE(7,"备货"),
     * PURCHASE_BATCH(8,"批量"),
     * PURCHASE_RECYCLE(9,"仅回收"),
     * PURCHASE_DISPLACE(10,"置换"),
     * PURCHASE_OTHER(11,"其他"),
     * REFUND(12,"退货"),
     */
    private Integer saleMode;

    /**
     * 订单来源
     */
    private Integer orderOrigin;

    /**
     * 供应商id非联系人id！！！
     */
    private Integer customerId;

    /**
     * 订单数量
     */
    private Integer orderNumber;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date outStoreTime;

    /**
     * 用于查询
     */
    private Integer belongId;

    /**
     * 是否分成
     */
    private Integer divideInto;

    /**
     * 销售渠道
     * T_MALL(2, "天猫国际"),
     * DOU_YIN(3, "抖音"),
     * STORE(4, "门店"),
     * SI_YU(8, "私域"),
     * XI_YI_SHOP(14, "稀蜴商城"),
     * JD(15, "京东"),
     * OTHER(0, "其它"),
     */
    private Integer clcId;


    /**
     * 客户
     */
    private String customerName;

    /**
     * 客户类型
     */
    private Integer customerType;


    /**
     * 订单归宿主体：谁销售的
     */
    private String belongSubjectName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 备注
     */
    private String remark;
}
