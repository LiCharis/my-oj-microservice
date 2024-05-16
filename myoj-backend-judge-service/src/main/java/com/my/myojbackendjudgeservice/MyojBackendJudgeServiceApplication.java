package com.my.myojbackendjudgeservice;

import com.my.myojbackendjudgeservice.rabbitmq.InitRabbitMq;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


@ComponentScan("com.my")  //扫描com.my下所有的bean对象
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.my.myojbackendserviceclient.service"})
public class MyojBackendJudgeServiceApplication {


    public static void main(String[] args) {
        //初始化消息队列
        InitRabbitMq.doInit();

        SpringApplication.run(MyojBackendJudgeServiceApplication.class, args);
    }

}
