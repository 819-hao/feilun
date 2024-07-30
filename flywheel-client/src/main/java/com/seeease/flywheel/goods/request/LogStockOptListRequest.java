package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/3/19 13:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogStockOptListRequest extends PageRequest implements Serializable {

    /**
     * 表身号
     */
    private String openingStockSn;
    /**
     * 老表身号
     */
    private String closingStockSn;
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    private String updatedBy;
}
