package com.my.myojbackendmodel.dto.question;

import lombok.Data;

/**
 * @author 黎海旭
 **/
@Data
public class JudgeConfig {
    /**
     * 时间限制(ms)
     */
    private Long timeLimit;

    /**
     * 内容限制(kb)
     */
    private Long memoryLimit;

    /**
     * 堆栈限制(kb)
     */
    private Long stackLimit;

}
