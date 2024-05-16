package com.my.myojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.my.myojbackendmodel.codesandbox.JudgeInfo;
import com.my.myojbackendmodel.dto.question.JudgeConfig;
import com.my.myojbackendmodel.entity.Question;
import com.my.myojbackendmodel.enums.JudgeInfoMessageEnum;


import java.util.List;

/**
 * @author 黎海旭
 * 默认判题策略
 **/
public class DefaultJudgeStrategy implements JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        List<String> correct_outputList = judgeContext.getCorrect_outputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();

        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        //取出代码沙箱执行结果后返回的时间及内存数据
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();

        JudgeInfo judgeResponse = new JudgeInfo();
        judgeResponse.setMemory(memory);
        judgeResponse.setTime(time);


        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPT;

        //如果输出数量不一致就返回答案错误
        if (correct_outputList.size() != outputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeResponse.setMessage(judgeInfoMessageEnum.getText());
            return judgeResponse;
        }

        //如果有一个对不上就是错误
        for (int i = 0; i < correct_outputList.size(); i++) {
            if (!correct_outputList.get(i).equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeResponse.setMessage(judgeInfoMessageEnum.getText());
                return judgeResponse;
            }

        }

        //3.判断题目限制条件是否合规 (堆栈限制先忽略)

        //取出问题的预设题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long memoryLimit = judgeConfig.getMemoryLimit();
        Long timeLimit = judgeConfig.getTimeLimit();

        if (memory > memoryLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeResponse.setMessage(judgeInfoMessageEnum.getText());
            return judgeResponse;
        }
        if (time > timeLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeResponse.setMessage(judgeInfoMessageEnum.getText());
            return judgeResponse;
        }

        judgeResponse.setMessage(judgeInfoMessageEnum.getText());
        
        return judgeResponse;
    }
}
