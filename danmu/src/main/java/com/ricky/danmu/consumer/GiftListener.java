package com.ricky.danmu.consumer;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.danmu.po.User;
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
public class GiftListener {
    private final UserService userService;
    private final ScoreDetailService scoreDetailService;

    @Autowired
    public GiftListener(UserService userService, ScoreDetailService scoreDetailService) {
        this.userService = userService;
        this.scoreDetailService = scoreDetailService;
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.DSG_QUERE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DY_EXCHANGE_KEY, type = ExchangeTypes.TOPIC),
                    key = RabbitConstant.DSG_KEY)
    )
    public void getGiftMsg(byte[] message) {
        System.out.println("来礼物了！！！！！！！！！！！！！！！！！！！！！！");
        String jsonStr = new String(message);
        JSONObject data = JSONObject.parseObject(jsonStr);
        String uid = data.getString("uid");
        String username = data.getString("username");
        userService.giftScore(uid,username);
    }


}
