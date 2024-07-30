package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 业务
 * @Date create in 2023/6/27 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpressCancelResult implements Serializable {
    
    /**
     * 快递单号
     */
    private String expressNo;
}
