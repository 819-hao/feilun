package com.seeease.flywheel.express.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/27 14:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressTrackRequest implements Serializable {

    /**
     * 顺丰单号
     */
    private String expressNo;

    /**
     * 业务单号
     */
    private String serialNo;
}
