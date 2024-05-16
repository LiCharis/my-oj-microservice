package com.my.myojbackendjudgeservice.judge.strategy;

import com.my.myojbackendmodel.codesandbox.JudgeInfo;
import com.my.myojbackendmodel.entity.Question;
import com.my.myojbackendmodel.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * @author 黎海旭
 * 判题所需要的上下文信息
 **/
@Data
public class JudgeContext {

    private List<String> correct_outputList;

    private List<String> outputList;

    private Question question;

    private JudgeInfo judgeInfo;

    private QuestionSubmit questionSubmit;
}
