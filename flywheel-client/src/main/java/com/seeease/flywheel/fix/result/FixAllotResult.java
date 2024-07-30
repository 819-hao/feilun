package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/13 14:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixAllotResult implements Serializable {

    /**
     * 维修单号
     */
    private String serialNo;

    private Integer stockId;
}
