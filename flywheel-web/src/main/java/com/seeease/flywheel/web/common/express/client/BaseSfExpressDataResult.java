package com.seeease.flywheel.web.common.express.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/27 14:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSfExpressDataResult implements Serializable {

    private String success;
    private String errorCode;
    private String errorMsg;
}
