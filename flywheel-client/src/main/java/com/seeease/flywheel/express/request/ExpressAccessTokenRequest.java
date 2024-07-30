package com.seeease.flywheel.express.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 获取token接口 业务
 * @Date create in 2023/6/26 14:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressAccessTokenRequest implements Serializable {

    /**
     * 顺丰单号
     */
    private String expressNo;

    /**
     * 业务单号
     */
    private String serialNo;
}
