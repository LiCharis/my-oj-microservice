package com.my.myojbackendjudgeservice.judge.codesandbox;


import com.my.myojbackendjudgeservice.judge.codesandbox.Impl.ExampleCodeSandBox;
import com.my.myojbackendjudgeservice.judge.codesandbox.Impl.RemoteCodeSandBox;
import com.my.myojbackendjudgeservice.judge.codesandbox.Impl.ThirdPartyCodeSandBox;

/**
 * @author 黎海旭
 * 可以根据输入的字符串参数生成指定的不同类型代码沙箱实例，此处运用了工厂设计模式
 * 采用了静态工厂设计模式
 **/
public class CodeSandBoxFactory {


    /**
     *
     * @param type 代码沙箱类型
     * @return
     */
    public static CodeSandBox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandBox();
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();

        }
    }


}
