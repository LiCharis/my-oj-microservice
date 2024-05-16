package com.my.myojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.myojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.my.myojbackendcommon.common.ErrorCode;
import com.my.myojbackendcommon.constant.CommonConstant;
import com.my.myojbackendcommon.exception.BusinessException;
import com.my.myojbackendcommon.utils.SqlUtils;
import com.my.myojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.my.myojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.my.myojbackendmodel.entity.Question;
import com.my.myojbackendmodel.entity.QuestionSubmit;
import com.my.myojbackendmodel.entity.User;
import com.my.myojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.my.myojbackendmodel.enums.QuestionSubmitStateEnum;
import com.my.myojbackendmodel.vo.QuestionSubmitVO;
import com.my.myojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.my.myojbackendquestionservice.service.QuestionSubmitService;
import com.my.myojbackendserviceclient.service.JudgeFeignClient;
import com.my.myojbackendserviceclient.service.QuestionFeignClient;
import com.my.myojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Li
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-10-29 17:24:50
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 判题服务
     * 懒加载，防止循环依赖(judgeServiceImp里面也装配了QuestionSubmitServiceImpl)
     */
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;


    /**
     * 消息队列生产者
     */
    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验传入的编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误!");
        }


        // 判断实体是否存在，根据类别获取实体
        Long questionId = questionSubmitAddRequest.getQuestionId();

        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        //设置初始状态 等待
        questionSubmit.setStatus(QuestionSubmitStateEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");


        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败!");
        }

        //todo 执行判题服务(异步进行),后面用RabbitMq来解耦判题和题目模块
//        CompletableFuture.runAsync(()-> judgeFeignClient.doJudge(questionSubmit.getId()));

        /**
         * 换做消息队列
         */
        myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmit.getId()));


        /**
         * 提交次数数量增加，
         */

        return questionSubmit.getId();
    }

//        // 锁必须要包裹住事务方法
//        QuestionSubmitService questionSubmitService = (QuestionSubmitService) AopContext.currentProxy();
//        synchronized (String.valueOf(userId).intern()) {
//            return questionSubmitService.doQuestionSubmitInner(userId, questionId);
//        }


    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        String status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "questionId", userId);
        queryWrapper.eq(QuestionSubmitStateEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //这里仅获取一次id即可，登陆的用户在controller层取然后传入就行，避免下面的分页查询反复调用此方法(getQuestionSubmitV)时反复查询数据库，造成性能浪费
        Long loginUserId = loginUser.getId();
        //todo 关联信息后面再完成
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
//        User user = null;
//        if (userId != null && userId > 0) {
//            user = userService.getById(userId);
//        }
//        //得到脱敏的user信息
//        UserVO userVO = userService.getUserVO(user);
//        //将信息赋给questionSubmitV0返回前端
//        questionSubmitVO.setUserVO(userVO);
//        // 2. 关联查询题目信息
//        Long questionId = questionSubmit.getQuestionId();
//        Question question = new Question();
//        if (questionId != null && questionId > 0){
//            question = questionService.getById(questionId);
//        }
//        //脱敏
//        QuestionVO questionVO = questionService.getQuestionVO(question,request);
//        questionSubmitVO.setQuestionVO(questionVO);

        //脱敏,除管理员外，普通用户只能看到本人的所有提交信息及非本人的提交问题的非代码信息
        if (!Objects.equals(userId, loginUserId) && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }

        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        //todo 尚未关联到用户、题目信息，待解决
        //处理脱敏,除管理员外，普通用户只能看到本人的所有提交信息及非本人的提交问题的非代码信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit ->
                getQuestionSubmitVO(questionSubmit, request, loginUser)
        ).collect(Collectors.toList());


        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




