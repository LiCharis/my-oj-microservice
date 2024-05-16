package com.my.myojbackendjudgeservice.judge.codesandbox;

import com.my.myojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 黎海旭
 * 采用代理类来调用代码沙箱的执行方法，方便我们输出日志而不用每次都去调用具体的沙箱实例且重复写日志输出
 * 运用了代理模式
 **/
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox{
    /**
     * 所选取的代码沙箱实例
     */
     private final CodeSandBox codeSandBox;


    /**
     * 显式定义构造器
     * @param codeSandBox
     */
    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    /**
     *
     * @param executeCodeRequest
     * @return
     */

    @Override
    public ExecuteCodeResponse codeExecute(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息: " + executeCodeRequest.toString());
        //调用执行方法
        ExecuteCodeResponse executeCodeResponse = codeSandBox.codeExecute(executeCodeRequest);
        log.info("代码沙箱响应信息: " + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
