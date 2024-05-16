package com.my.myojbackendserviceclient.service;


import com.my.myojbackendmodel.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 黎海旭
 * 判题服务
 **/

@FeignClient(name = "myoj-backend-judge-service",path = "/api/judge/inner")
public interface JudgeFeignClient {

    @PostMapping("/doJudge")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
