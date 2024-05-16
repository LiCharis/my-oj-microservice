package com.my.myojbackendjudgeservice.judge.strategy;


import com.my.myojbackendmodel.codesandbox.JudgeInfo;

/**
 * @author 黎海旭
 * 判题策略，根据不同的语言执行不同的判题逻辑
 * 运用了策略模式
 **/
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
