package com.my.myojbackendjudgeservice.judge.codesandbox.Impl;


import com.my.myojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.my.myojbackendmodel.codesandbox.JudgeInfo;
import com.my.myojbackendmodel.enums.JudgeInfoMessageEnum;
import com.my.myojbackendmodel.enums.QuestionSubmitStateEnum;

import java.util.List;

/**
 * @author 黎海旭
 * 示例代码沙箱，为了示例跑通代码用的
 **/
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse codeExecute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱");
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();

        /**
         * 这里省略了提交给代码沙箱执行提交代码的过程，仅是为了打通其余的流程
         */

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPT.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        ExecuteCodeResponse executeCodeResponse = ExecuteCodeResponse.builder()
                .outputList(inputList)
                .message("代码执行成功!")
                .status(QuestionSubmitStateEnum.SUCCEED.getValue())
                .judgeInfo(judgeInfo).build();


        return executeCodeResponse;
    }
}
