package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
public class PurchaseReturnListRequest extends PageRequest {
    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购退货类型
     */
    private Integer purchaseReturnType;


    private Integer purchaseReturnState;

    private String customerName;

    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    private String createdBy;

    private Integer storeId;

    private String serialNo;

    private List<Integer> stockIdList;

}
