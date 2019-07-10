package com.ricky.danmu.consumer;

import com.alibaba.fastjson.JSONObject;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.utils.WriteFileUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: ricky
 * @Date: 2019/7/10 10:42
 */
@Component
public class WriteListener {

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.WRITE_QUERE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DY_EXCHANGE_KEY, type = ExchangeTypes.TOPIC),
                    key = RabbitConstant.WRITE_KEY)
    )
    public void writeFile(JSONObject data) {
        try {
            //休眠3秒
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String content = data.getString("content");
        String path = "";
        WriteFileUtils.writeDataToFile(path,content);
    }
}
