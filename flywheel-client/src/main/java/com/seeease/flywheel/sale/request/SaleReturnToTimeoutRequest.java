package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/2/22 10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnToTimeoutRequest implements Serializable {

    /**
     * 日期 超时日期
     */
    private String timeoutDate;

    /**
     * 超时间隔 超时天数
     */
    private Integer timeoutDay;

    /**
     * 接收方id
     */
    private List<Integer> storeIdList;

    private Integer roleId;
}
