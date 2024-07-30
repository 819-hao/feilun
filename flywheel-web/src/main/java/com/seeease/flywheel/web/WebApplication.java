package com.seeease.flywheel.web;

import com.seeease.springframework.exception.EnableGlobalExceptionHandler;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Tiro
 * @date 2023/1/7
 */
@EnableDubbo
@MapperScan("com.seeease.flywheel.web.infrastructure.mapper")
@SpringBootApplication(scanBasePackages = {"com.seeease.flywheel.web", "com.alibaba.cola"})
@EnableScheduling
@EnableAsync
@EnableGlobalExceptionHandler
@EnableDiscoveryClient
public class WebApplication {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled","false");
        SpringApplication.run(WebApplication.class, args);
        System.out.println("=========================启动完成=========================");
    }
}
