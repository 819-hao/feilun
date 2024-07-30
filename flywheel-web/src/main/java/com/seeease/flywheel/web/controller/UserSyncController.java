package com.seeease.flywheel.web.controller;

import com.google.common.collect.Lists;
import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.infrastructure.external.firework.WorkflowService;
import com.seeease.flywheel.web.infrastructure.service.UserSyncService;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/29
 */
@Slf4j
@RestController
@RequestMapping("/userSync")
public class UserSyncController {
    @Resource
    private UserSyncService userSyncService;

    @Resource
    private WorkflowService workflowService;

    @RequestMapping("/sync")
    public SingleResponse sync(@RequestParam("userId") String userId, @RequestParam("storeId") Long storeId) {
        List<User> users = userSyncService.selectUser(userId, storeId);
        Lists.partition(users, 40)
                .forEach(t -> workflowService.saveUser(t));
        return SingleResponse.buildSuccess();
    }
}
