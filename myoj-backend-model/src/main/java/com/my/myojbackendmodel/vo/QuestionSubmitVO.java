package com.my.myojbackendmodel.vo;

import cn.hutool.json.JSONUtil;

import com.my.myojbackendmodel.codesandbox.JudgeInfo;
import com.my.myojbackendmodel.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 黎海旭
 * 返回给前端的问题提交类
 **/
@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息(json对象)
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态(0 - 待判题， 1 - 判题中)，2 - 成功， 3 - 失败)
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     *用户脱敏信息
     */
    private UserVO userVO;

    /**
     * 题目脱敏信息
     */
    private QuestionVO questionVO;


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        //同名属性拷贝
        BeanUtils.copyProperties(questionVO, questionSubmit);

        JudgeInfo judgeInfo = questionVO.getJudgeInfo();
        if (judgeInfo != null){
            //将judgeConfig对象转为字符串
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        //反过来
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
        questionSubmitVO.setJudgeInfo(judgeInfo);
        return questionSubmitVO;
    }


}
