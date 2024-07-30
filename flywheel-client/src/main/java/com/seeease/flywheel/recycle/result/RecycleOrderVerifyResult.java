package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/8 09:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleOrderVerifyResult implements Serializable {

    /**
     * 工作流参数
     */
//    private ProcessVerifyResult process;

    /**
     *
     */
    private Boolean verify;

//    private Integer balance;
}
