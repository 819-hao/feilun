package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description demo
 * @Date create in 2023/6/25 13:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressCreateResult implements Serializable {

    /**
     * 快递单号
     */
    private String expressNo;
}
