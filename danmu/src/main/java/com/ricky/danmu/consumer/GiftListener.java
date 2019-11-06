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

/**
 * @Description: 礼物弹幕监听器，当有人在房间送礼物后可以进行一些操作
 * 下面是给指定的用户增加用户积分（该积分只保存在自己的数据库中）
 * @Author: ricky
 * @Date: 2019/11/6 17:14
 */
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
