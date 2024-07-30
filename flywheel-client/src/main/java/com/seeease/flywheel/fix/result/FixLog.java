package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/17 15:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixLog implements Serializable {

    /**
     * 0 维修建单
     * 1 维修分配
     * 2 维修(完成或者送外)
     * 3 维修接修
     * 4 维修取消
     */
    private Integer fixNode;

    private String createdBy;

    private String createdTime;

}
