package com.my.myojbackendjudgeservice.judge;


import com.my.myojbackendmodel.entity.QuestionSubmit;

/**
 * @author 黎海旭
 * 判题服务
 **/

public interface JudgeService {

    QuestionSubmit doJudge(long questionSubmitId);
}
