
package com.ricky.player.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.player.dao.UserDao;
import com.ricky.player.po.ScoreDetail;
import com.ricky.player.po.User;
import com.ricky.player.service.ScoreDetailService;
import com.ricky.player.service.UserService;
import com.ricky.player.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service

public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ScoreDetailService scoreDetailService;

    /**
     * 查询用户积分
     * @param uid   用户ID
     * @param username  用户名称
     */
    @Override
    public void queryScore(String uid, String username) {
        //判断用户是否为空,如果对象为空,则新建一个
        User user = this.selectOne(new EntityWrapper<User>().eq("uuid", uid));
        JSONObject data = new JSONObject();
        if (null == user) {
            user.setScore(0);
            user.setUuid(uid);
            user.setUsername(username);
            this.insert(user);
            //发送MQ到队列去写弹幕显示文件
            String content = "用户 "+ username + " 的积分数：0";
            data.put("content",content);
            rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.WRITE_KEY,data);
            return;
        }
        String content = "用户 "+ user.getUsername() + " 的积分数：" + user.getScore();
        data.put("content",content);
        rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.WRITE_KEY,data);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void loginSendScore(String uid, String username) {
        //判断用户是否为空,如果对象为空,则新建一个
        User user = this.selectOne(new EntityWrapper<User>().eq("uuid", uid));
        JSONObject data = new JSONObject();
        if (null == user) {
            user.setScore(0);
            user.setUuid(uid);
            user.setUsername(username);
            this.insert(user);
            //设置打卡限制，1个小时打卡一次
            redisUtils.set("Sign"+uid,"Sign"+uid,60*60);
            //发送MQ到队列去写弹幕显示文件
            String content = "用户 "+ username + " 打卡成功！";
            data.put("content",content);
            rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.WRITE_KEY,data);
            return;
        }
        String isExist = redisUtils.get("SignIn" + uid);
        //如果为空，则代表可以继续打卡，不为空则无效
        if(StringUtils.isEmpty(isExist)){
            //增加用户的分数
            Integer score = 300;
            Integer result1 = this.baseMapper.updateAddScore(300, uid);
            if(result1<0){
                throw new RuntimeException();
            }
            //增加一条积分消费记录
            ScoreDetail detail = new ScoreDetail();
            detail.setOperationType(OperationType.Login.getType());
            detail.setScore(score);
            detail.setUserId(user.getId());
            detail.setUuid(uid);
            boolean result2 = scoreDetailService.insert(detail);
            if(!result2){
                throw new RuntimeException();
            }
            //加分的同时，设置一个限制
            redisUtils.set("Sign"+uid,"Sign"+uid,60*60);
            //发送MQ到队列去写弹幕显示文件
            String content = "用户 "+ username + " 打卡成功！";
            data.put("content",content);
            rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.WRITE_KEY,data);
        }
    }
}

