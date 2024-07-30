package com.seeease.flywheel.web.controller.xianyu.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Data
public class QiMenBaseRequest implements Serializable {
    private String method;
    private String app_key;
    private String session;
    private String timestamp;
    private String v;
    private String sign_method;
    private String sign;
    private String format;
    private String simplify;
    private String customerId;
}
