package com.seeease.flywheel.allocate.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateListRequest extends PageRequest implements Serializable {

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 库存id
     */
    private List<Integer> stockIds;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 调出方
     */
    private Integer fromId;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调拨来源
     */
    private Integer allocateSource;

    /**
     * 调拨状态
     */
    private Integer allocateState;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 登陆用户门店id
     */
    private Integer shopId;
}
