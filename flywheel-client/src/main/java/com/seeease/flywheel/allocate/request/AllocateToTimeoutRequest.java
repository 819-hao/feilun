package com.seeease.flywheel.allocate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateToTimeoutRequest implements Serializable {

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
