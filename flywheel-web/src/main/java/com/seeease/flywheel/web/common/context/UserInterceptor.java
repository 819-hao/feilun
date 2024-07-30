package com.seeease.flywheel.web.common.context;

import com.alibaba.fastjson.JSONObject;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userHeader = request.getHeader("userVO");

        if (StringUtils.isNotEmpty(userHeader)) {
            userHeader = URLDecoder.decode(userHeader);
            //userVo本地线程缓存，方便后面操作获取
            UserVO user = JSONObject.parseObject(userHeader, UserVO.class);
            LoginUser loginUser = LoginUser.builder()
                    .id(user.getId())
                    .userid(user.getUserid())
                    .userName(user.getUserName())
                    .roles(Optional.ofNullable(user.getRoles())
                            .map(t -> t.stream().map(r ->
                                            LoginRole.builder()
                                                    .roleId(r.getRoleId())
                                                    .roleName(r.getRoleName())
                                                    .build())
                                    .collect(Collectors.toList())).orElseGet(null))
                    .store(Optional.ofNullable(user.getStores())
                            .map(t -> t.stream().map(s ->
                                    LoginStore.builder()
                                            .id(s.getId().intValue())
                                            .name(s.getName())
                                            .build()
                            ).findFirst().orElse(null))
                            .orElse(null)
                    ).build();
            UserContext.setUser(loginUser);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse
            response, Object handler, Exception ex) {
        //移除用户信息
        UserContext.clear();
    }
}
