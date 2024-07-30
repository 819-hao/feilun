package com.seeease.flywheel.express.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description demo
 * @Date create in 2023/6/26 14:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressAccessTokenResult implements Serializable {

    /**
     * 顺丰单号
     */
    private String expressNo;

    /**
     * 临时token
     */
    private String accessToken;
}
