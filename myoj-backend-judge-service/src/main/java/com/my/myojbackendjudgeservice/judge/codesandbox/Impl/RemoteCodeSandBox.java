package com.my.myojbackendjudgeservice.judge.codesandbox.Impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.my.myojbackendcommon.common.ErrorCode;
import com.my.myojbackendcommon.exception.BusinessException;
import com.my.myojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.my.myojbackendmodel.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 黎海旭
 * 远程代码沙箱(自定动手写)
 **/
public class RemoteCodeSandBox implements CodeSandBox {

    /**
     * 定义鉴权请求头和密钥,先保证接口不会被外来请求调用(服务内部调用阶段)
     */
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse codeExecute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://127.0.0.1:8105/executeCode";
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_HEADER)
                .body(jsonStr)
                .execute()
                .body();

        if (StringUtils.isAnyEmpty(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "远程代码沙箱执行错误");
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
