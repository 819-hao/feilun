package com.seeease.flywheel.serve;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
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
@SpringBootApplication
@MapperScan("com.seeease.flywheel.serve.*.mapper")
@EnableGlobalExceptionHandler
@EnableScheduling
@EnableAsync
@EnableDiscoveryClient
public class ServeApplication {
    public static void main(String[] args) {
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(ServeApplication.class, args);
        System.out.println("=========================启动完成=========================");
    }
}
