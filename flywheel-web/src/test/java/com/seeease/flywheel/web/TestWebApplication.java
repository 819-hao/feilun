package com.seeease.flywheel.web;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.entity.UserRole;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.infrastructure.mapper.UserSyncMapper;
import com.seeease.flywheel.web.infrastructure.service.WorkflowStartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/29
 */
@RunWith(SpringRunner.class)
@EnableTransactionManagement
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestWebApplication {
    @Resource
    private UserSyncMapper userSyncMapper;
    @Resource
    private WorkflowStartService workflowStartService;
    
    @Test
    public void test(){


        WorkflowStart s = workflowStartService.getById(1);

        System.out.println(s.getProcessVariables());
//
//        List<User> s = userSyncMapper.selectUser(null);
//        System.out.println(JSONObject.toJSONString(s));
//
    }
}
