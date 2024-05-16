package com.my.myojbackendjudgeservice.judge.codesandbox.Impl;


import com.my.myojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * @author 黎海旭
 * 第三方代码沙箱
 **/
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse codeExecute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
