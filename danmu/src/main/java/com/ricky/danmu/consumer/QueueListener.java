package com.ricky.danmu.consumer;

import com.alibaba.fastjson.JSONObject;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.danmu.service.FilmService;
import com.ricky.danmu.service.ScoreDetailService;
import com.ricky.danmu.service.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {
    private final UserService userService;
    private final FilmService filmService;
    private final ScoreDetailService scoreDetailService;

    @Autowired
    public QueueListener(UserService userService, FilmService filmService, ScoreDetailService scoreDetailService) {
        this.userService = userService;
        this.filmService = filmService;
        this.scoreDetailService = scoreDetailService;
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.CHAT_QUERE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DY_EXCHANGE_KEY, type = ExchangeTypes.TOPIC),
                    key = RabbitConstant.CHAT_KEY)
    )
    public void getChatMsg(byte[] message) {
        System.out.println("接受到消息了！");
        String jsonStr = new String(message);
        JSONObject data = JSONObject.parseObject(jsonStr);
        // 判断操作类型
        Integer order = data.getInteger("order");
        if (null == order) {
            return;
        }
        String uid = data.getString("uid");
        String username = data.getString("username");
        String movieName = data.getString("movieName");
        Integer score = data.getInteger("score");

        //点播电影：10 ，查询积分：20 ,打卡：30
        if (OperationType.PlayMovie.getType().equals(order)) {
            filmService.playMovie(uid,username,movieName,score);
        } else if (OperationType.QueryScore.getType().equals(order)) {
            userService.queryScore(uid,username);
        }else if(OperationType.Login.getType().equals(order)){
            userService.loginSendScore(uid,username);
        }
        System.out.println("chatMsg:"+data.toString());
    }


}
