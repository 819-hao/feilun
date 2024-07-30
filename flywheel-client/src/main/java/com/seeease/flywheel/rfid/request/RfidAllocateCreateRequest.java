package com.seeease.flywheel.rfid.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidAllocateCreateRequest implements Serializable {
    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调拨来源
     */
    private Integer allocateSource;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 调入仓库
     */
    private Integer toStoreId;

    /**
     * 采购备注
     */
    private String remarks;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 品牌调拨任务
     */
    private boolean isBrandTask;

    /**
     * 商品编码
     */
    private List<String> wnoList ;

    /**
     * 快递单号
     */
    private String expressNo;


}
