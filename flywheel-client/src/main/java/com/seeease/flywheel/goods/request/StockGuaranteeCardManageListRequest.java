package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
public class StockGuaranteeCardManageListRequest extends PageRequest {

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 调拨单号
     */
    private String allocateNo;

    /**
     * 调拨状态：0-未调拨，1-已调拨
     */
    private Integer allocateState;
}
