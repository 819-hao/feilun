package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/3 11:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixDelayRequest implements Serializable {

    private Integer fixId;

    private String finishTime;

    private String timeoutMsg;
}
