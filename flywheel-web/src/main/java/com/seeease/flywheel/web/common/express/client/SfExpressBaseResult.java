package com.seeease.flywheel.web.common.express.client;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Data
public class SfExpressBaseResult implements Serializable {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;
}
