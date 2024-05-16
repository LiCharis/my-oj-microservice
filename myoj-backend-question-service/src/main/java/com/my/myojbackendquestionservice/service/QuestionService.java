package com.my.myojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.myojbackendmodel.dto.question.QuestionQueryRequest;
import com.my.myojbackendmodel.entity.Question;
import com.my.myojbackendmodel.vo.QuestionVO;


import javax.servlet.http.HttpServletRequest;

/**
* @author Li
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-10-29 17:24:29
*/
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param Question
     * @param add
     */
    void validQuestion(Question Question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param Question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question Question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param QuestionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> QuestionPage, HttpServletRequest request);

}
