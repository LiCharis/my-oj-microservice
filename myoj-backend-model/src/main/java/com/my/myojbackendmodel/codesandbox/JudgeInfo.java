package com.my.myojbackendmodel.codesandbox;

import lombok.Data;

/**
 * @author 黎海旭
 **/
@Data
public class JudgeInfo {
    /**
     * 程序执行结果信息
     */
    private String message;

    /**
     * 消耗内存(kb)
     */
    private Long memory;

    /**
     * 消耗时间(ms)
     */
    private Long time;
}
