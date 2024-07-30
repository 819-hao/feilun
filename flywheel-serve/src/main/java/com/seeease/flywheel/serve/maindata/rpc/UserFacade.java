package com.seeease.flywheel.serve.maindata.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.maindata.IUserFacade;
import com.seeease.flywheel.maindata.entity.UserInfo;
import com.seeease.flywheel.serve.maindata.convert.UserConverter;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@DubboService(version = "1.0.0")
public class UserFacade implements IUserFacade {
    @Resource
    private UserService userService;

    @Override
    public List<UserInfo> listUser(List<String> qwUserIdList) {
        if (CollectionUtils.isEmpty(qwUserIdList)) {
            return Collections.EMPTY_LIST;
        }
        return userService.list(Wrappers.<User>lambdaQuery()
                        .in(User::getUserid, qwUserIdList))
                .stream()
                .map(UserConverter.INSTANCE::convertUserInfo)
                .collect(Collectors.toList());

    }

}
