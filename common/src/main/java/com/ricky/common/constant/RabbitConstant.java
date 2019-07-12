package com.ricky.common.constant;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConstant {

    // 弹幕队列
    public final static String CHAT_QUERE = "chat.queue";
    // 礼物队列
    public final static String DSG_QUERE = "dsg.queue";
    // 重链接队列
    public final static String RECONNECT_QUERE = "reconnect.queue";
    // 写弹幕文件队列
    public final static String WRITE_QUERE = "write.queue";
    // 绑定的值
    public static final String CHAT_KEY = "#.chat.#";
    // 绑定的值
    public static final String DSG_KEY = "#.dsg.#";
    // 绑定的值
    public static final String RECONNECT_KEY = "#.reconnect.#";
    // 绑定的值
    public static final String WRITE_KEY = "#.write.#";
    // 绑定的值
    public static final String DY_EXCHANGE_KEY = "douyu.exchange";

}
