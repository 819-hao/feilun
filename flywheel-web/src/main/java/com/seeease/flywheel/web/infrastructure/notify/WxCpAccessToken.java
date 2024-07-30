package com.seeease.flywheel.web.infrastructure.notify;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Data
public class WxCpAccessToken implements Serializable {

    @JSONField(name = "access_token")
    private String accessToken;
}
