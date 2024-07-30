//package com.seeease.flywheel.web.common.context;
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.base.Ticker;
//import com.google.common.cache.*;
//import com.seeease.flywheel.web.entity.ApiLog;
//import com.seeease.flywheel.web.infrastructure.mapper.ApiLogMapper;
//import com.seeease.springframework.context.LoginUser;
//import com.seeease.springframework.context.UserContext;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author Mr. Du
// * @Description 请求拦截
// * @Date create in 2023/7/26 09:43
// */
//
//@Aspect
//@Component
//@Slf4j
//public class ControllerInterceptor {
//
//    @Resource
//    private ApiLogMapper mapper;
//
//    /**
//     * 初始key/value
//     */
//    private Cache<Long, ControllerVO> CONTROLLER_VO_CACHE = CacheBuilder.newBuilder()
//            .ticker(Ticker.systemTicker())
//            //容量大小
//            .maximumSize(500)
//            // 设置内部哈希表的大小
//            .initialCapacity(16)
//            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
//            //回收
//            .expireAfterWrite(10, TimeUnit.SECONDS)
//            //移除项监听
//            .removalListener(
//                    RemovalListeners.asynchronous(new RemovalListener<Long, ControllerVO>() {
//                        @Override
//                        public void onRemoval(RemovalNotification<Long, ControllerVO> removalNotification) {
//                            insert(removalNotification.getValue());
//                        }
//                    }, new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors())))
//            .build();
//
//    @Before("execution(public * com.seeease.flywheel.web.controller.*.*(..))")
//    public void handlerControllerMethod(JoinPoint joinPoint) {
//
//        //接收请求,记录请求内容
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        long currentTimeMillis = System.currentTimeMillis();
//        CONTROLLER_VO_CACHE.put(currentTimeMillis, ControllerVO.builder()
//                //登录用户
//                .loginUser(UserContext.getUser())
//                .uri(request.getRequestURI().toString())
//                .currentTime(currentTimeMillis)
//                .classMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName())
//                .method(request.getMethod())
//                .ip(request.getRemoteAddr())
//                .build());
//        CONTROLLER_VO_CACHE.size();
//    }
//
//    void insert(ControllerVO controllerVO) {
//        ApiLog apiLog = new ApiLog();
//        LoginUser loginUser = controllerVO.getLoginUser();
//
//        apiLog.setLoginUser(JSONObject.toJSONString(loginUser));
//        apiLog.setClassMethod(controllerVO.getClassMethod());
//        apiLog.setRequestMethod(controllerVO.getMethod());
//        apiLog.setNowTime(controllerVO.getCurrentTime());
//        apiLog.setRemoteUri(controllerVO.getUri());
//        apiLog.setRemoteAddr(controllerVO.getIp());
//        apiLog.setUserId(loginUser.getId());
//        apiLog.setUserName(loginUser.getUserName());
//        apiLog.setStoreId(loginUser.getStore().getId());
//        apiLog.setStoreName(loginUser.getStore().getName());
//        mapper.insert(apiLog);
//    }
//}
