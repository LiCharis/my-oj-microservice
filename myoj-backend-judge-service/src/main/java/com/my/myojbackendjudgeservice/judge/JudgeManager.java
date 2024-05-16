package com.my.myojbackendjudgeservice.judge;

import com.my.myojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.my.myojbackendjudgeservice.judge.strategy.JavaJudgeStrategy;
import com.my.myojbackendjudgeservice.judge.strategy.JudgeContext;
import com.my.myojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.my.myojbackendmodel.codesandbox.JudgeInfo;
import com.my.myojbackendmodel.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * @author 黎海旭
 * 判题管理，方便选取不同的判题策略
 **/
@Service
public class JudgeManager {

    public JudgeInfo doJudgeManage(JudgeContext judgeContext){

        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        //拿到提交的语言，以此来选择不同的判题策略
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (language.equals("java")){
            judgeStrategy = new JavaJudgeStrategy();

        }
        JudgeInfo judgeInfo = judgeStrategy.doJudge(judgeContext);

        return judgeInfo;
    }
}
