package com.ricky.danmu.config;

import com.alibaba.fastjson.JSONObject;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.common.utils.DanmuUtils;
import com.yycdev.douyu.sdk.DouYuClient;
import com.yycdev.douyu.sdk.MessageListener;
import com.yycdev.douyu.sdk.entity.ChatMsg;
import com.yycdev.douyu.sdk.entity.DgbMsg;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description: 项目启动时的操作，启动项目后并自动连接弹幕服务器
 * @Author: ricky
 * @Date: 2019/11/6 17:13
 */
@Component
public class InitConfig implements ApplicationRunner {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${client.host}")
    private String host;
    @Value("${client.roomId}")
    private String roomId;
    @Value("${client.port}")
    private Integer port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //以下是消息监听器
        DouYuClient client = new DouYuClient(host, port, roomId);
        //监听礼物弹幕
        client.registerMessageListener(new MessageListener<DgbMsg>() {
            @Override
            public void read(DgbMsg message) {
                //用户ID
                String uid = message.getUid();
                //用户昵称
                String nn = message.getNn();
                System.out.println("礼物！礼物！礼物！礼物！---------"+uid);
                JSONObject data = new JSONObject();
                data.put("uid",uid);
                data.put("username",nn);
                rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.DSG_KEY,data.toString().getBytes());
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
