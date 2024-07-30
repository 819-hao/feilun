package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseExportResult implements Serializable {
    /**
     * 采购单line id
     */
    private Integer id;
    /**
     * 打款人姓名
     */
    private String contactName = "";
    /**
     * 打款人电话
     */
    private String contactPhone = "";
    /**
     * 采购单号
     */
    private String serialNo;
    /**
     * 商品编码
     */
    private String sn;
    /**
     * 商品标题
     */
    private String productTitle;
    /**
     * 成色
     */
    private String fineness;
    /**
     * 采购价
     */
    private String purchasePrice;
    /**
     * 创建人
     */
    private String createdTime;
    /**
     * 采购人
     */
    private String purchaseBy = "";
    /**
     * 状态
     */
    private String status;
}
