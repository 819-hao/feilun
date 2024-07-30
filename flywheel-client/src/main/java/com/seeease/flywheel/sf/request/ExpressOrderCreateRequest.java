package com.seeease.flywheel.sf.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressOrderCreateRequest implements Serializable {

    private String serialNo;

    private String sonSerialNo;

    private String expressNo;

    private String requestId;

    /**
     * 1.顺丰 2。抖音
     */
    private Integer expressChannel;

    private Long douYinShopId;
}
