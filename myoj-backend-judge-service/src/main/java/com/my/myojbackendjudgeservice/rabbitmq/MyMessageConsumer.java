package com.my.myojbackendjudgeservice.rabbitmq;

import com.my.myojbackendjudgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author 黎海旭
 **/
@Component
@Slf4j
public class MyMessageConsumer {
    /**
     * 消费者，也就是调用doJudge方法处理发送的提交题目记录
     */
    @Resource
    private JudgeService judgeService;

    //指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("receiverMessage message = {}",message);

        long questionSubmitId = Long.parseLong(message);


        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            channel.basicNack(deliveryTag,false,false);
        }

    }
}
