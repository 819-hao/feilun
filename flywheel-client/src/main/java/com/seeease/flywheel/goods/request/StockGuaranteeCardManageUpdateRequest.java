package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
public class StockGuaranteeCardManageUpdateRequest implements Serializable {

    private Integer id;

    private List<Integer> idList;

    /**
     * 调拨单号
     */
    private String allocateNo;

    /**
     * 调拨状态：0-未调拨，1-已调拨
     */
    private Integer allocateState;

    /**
     * 调出时间
     */
    private String outTime;

    /**
     * 备注
     */
    private String remarks;

}
