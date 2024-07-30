//package com.seeease.flywheel.serve.helper.ai_image_model;
//
//
//import com.alibaba.nacos.api.config.ConfigService;
//import com.alibaba.nacos.api.config.listener.Listener;
//import com.alibaba.nacos.api.exception.NacosException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//@Component
//public class AiModelNacosListener {
//    @Resource
//    private ConfigService configService;
//
//    @Resource
//    private BaiDuAiModel baiDuAIModel;
//
//    @Value("${nacos.config.dataIds}")
//    private String dataId;
//
//    @Value("${nacos.config.group}")
//    private String group;
//
//    public void init() throws NacosException {
//        // 监听 Nacos 配置的变化
//        configService.addListener(dataId, group, new Listener() {
//            @Override
//            public Executor getExecutor() {
//                return Executors.newSingleThreadExecutor();
//            }
//
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                // 配置变化时的操作
//                baiDuAIModel.init(); // 重新初始化 baiduClient
//            }
//        });
//    }
//}
