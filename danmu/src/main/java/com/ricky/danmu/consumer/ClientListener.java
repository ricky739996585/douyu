package com.ricky.danmu.consumer;

import com.alibaba.fastjson.JSONObject;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.common.utils.DanmuUtils;
import com.yycdev.douyu.sdk.DouYuClient;
import com.yycdev.douyu.sdk.MessageListener;
import com.yycdev.douyu.sdk.entity.ChatMsg;
import com.yycdev.douyu.sdk.entity.DgbMsg;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.RECONNECT_QUERE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DY_EXCHANGE_KEY, type = ExchangeTypes.TOPIC),
                    key = RabbitConstant.RECONNECT_KEY)
    )
    public void reconnectClient(byte[] message) {
        System.out.println("客户端正在重新连接！");
        String jsonStr = new String(message);
        JSONObject data = JSONObject.parseObject(jsonStr);
        System.out.println(data.toString());
        //以下是消息监听器
        DouYuClient client = new DouYuClient("openbarrage.douyutv.com", 8601, "6639458");
        //监听礼物弹幕
        client.registerMessageListener(new MessageListener<DgbMsg>() {
            @Override
            public void read(DgbMsg message) {

            }
        });
        //监听弹幕消息
        client.registerMessageListener(new MessageListener<ChatMsg>() {
            @Override
            public void read(ChatMsg message) {
                //弹幕消息
                String content = message.getTxt();
                //用户ID
                String uid = message.getUid();
                //用户昵称
                String nn = message.getNn();
                System.out.println("弹幕内容："+content);
                JSONObject data = DanmuUtils.validate(content);
                if(null!=data){
                    Integer order = data.getInteger("order");
                    JSONObject param = new JSONObject();

                    param.put("uid",uid);
                    param.put("username",nn);
                    param.put("order",order);
                    if(OperationType.PlayMovie.getType().equals(order)){
                        param.put("movieName",data.getString("movieName"));
                        param.put("score",data.getDouble("score"));
                    }
                    System.out.println(param.toString());
                    rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.CHAT_KEY,param.toString().getBytes());
                }

            }
        });
        client.login();
        try{
            client.sync();
        }catch (Exception e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code","error");
            rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.RECONNECT_KEY,jsonObject.toString().getBytes());
            System.out.println("断开链接！");
        }

    }


}
