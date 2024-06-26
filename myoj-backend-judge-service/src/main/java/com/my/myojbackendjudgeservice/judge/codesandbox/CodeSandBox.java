package com.my.myojbackendjudgeservice.judge.codesandbox;


import com.my.myojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * @author 黎海旭
 **/
public interface CodeSandBox {
    /**
     * 定义接口，提高程序通用性，以后如果要用别的代码沙箱的话就实现这个接口就好
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse codeExecute(ExecuteCodeRequest executeCodeRequest);
}
