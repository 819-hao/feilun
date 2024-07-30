package com.seeease.flywheel.web.common.context;

import com.seeease.springframework.context.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/26 10:03
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ControllerVO implements Serializable {

    private String uri;

    private Long currentTime;

    private String ip;

    private String method;

    private String classMethod;

    private LoginUser loginUser;
}
