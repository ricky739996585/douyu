package com.ricky.player.utils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

/**
 * @Author: ricky
 * @Date: 2019/7/12 9:15
 */
public class RabbitMQUtils {
    //通过获取配置文件来获取配置
    private static final String RESOURCE_BUNDLE_BASE_NAME = "strings/config";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME);
    private static final String HOST = resourceBundle.getString("rabbitmq.host");
    private static final Integer PORT = Integer.valueOf(resourceBundle.getString("rabbitmq.port"));
    private static final String USERNAME = resourceBundle.getString("rabbitmq.username");
    private static final String PASSWORD = resourceBundle.getString("rabbitmq.password");
    private static final String VIRTUAL_HOST = resourceBundle.getString("rabbitmq.virtualHost");

    public static Connection getConnection() throws IOException, TimeoutException {
        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        //设置vhost
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection = factory.newConnection();
        return connection;

    }

    public static void SendMsg(Connection conn, String exchangeKey, String rountingKey, JSONObject data) throws IOException, TimeoutException {
        Channel channel = conn.createChannel();
        byte[] body = data.toString().getBytes();
        channel.basicPublish(exchangeKey, rountingKey, null, body);
        channel.close();
        conn.close();
    }

    public static void ConsumerMsg(Connection conn, String queueName) throws IOException {
        Channel channel = conn.createChannel();
        //声明通道
        channel.queueDeclare(queueName, false, false, false, null);
        //定义消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                // (process the message components here ...)
                channel.basicAck(deliveryTag, false);
            }
        };
        //消费消息
        channel.basicConsume(queueName, true, consumer);

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangKey = "douyu.exchange";
        String rountingKey = "#.write.#";
        Connection conn = getConnection();
        JSONObject data = new JSONObject();
        data.put("writeType",1);

        SendMsg(conn,exchangKey,rountingKey,data);


    }

}
