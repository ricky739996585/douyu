
package com.ricky.danmu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.OperationType;
import com.ricky.common.enums.WriteType;
import com.ricky.danmu.dao.FilmDao;
import com.ricky.danmu.dao.UserDao;
import com.ricky.danmu.po.Film;
import com.ricky.danmu.po.ScoreDetail;
import com.ricky.danmu.po.User;
import com.ricky.danmu.service.FilmService;
import com.ricky.danmu.service.ScoreDetailService;
import com.ricky.danmu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service

public class FilmServiceImpl extends ServiceImpl<FilmDao, Film> implements FilmService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ScoreDetailService scoreDetailService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 点播操作
     * @param uid 用户ID
     * @param username 用户昵称
     * @param movieName 电影名称
     * @param score
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void playMovie(String uid, String username, String movieName, Integer score) {
        //判断用户是否为空,如果对象为空,则新建一个
        User user = userService.selectOne(new EntityWrapper<User>().eq("uuid", uid));
        if(null == user){
            user = new User();
            user.setScore(0);
            user.setUuid(uid);
            user.setUsername(username);
            userService.insert(user);
            return;
        }

        //判断电影是否在播放列表,不存在则退出
        if(StringUtils.isEmpty(movieName)){
            return;
        }
        Film film = this.selectOne(new EntityWrapper<Film>().eq("name", movieName));
        if(null == film){
            return;
        }
        //若不为空，则判断积分是否足够
        Integer uScore = user.getScore();
        if(score>uScore){
            return;
        }
        //减少用户积分
        Integer result1 = userDao.updateCutScore(score, uid);
        if(result1<0){
            throw new RuntimeException();
        }
        //并增加电影的积分
        Integer result2 = this.baseMapper.updateDemand(score, movieName);
        if(result2<0){
            throw new RuntimeException();
        }
        //增加一条积分消费记录
        ScoreDetail detail = new ScoreDetail();
        detail.setOperationType(OperationType.PlayMovie.getType());
        detail.setScore(score);
        detail.setUserId(user.getId());
        detail.setUuid(uid);
        boolean result3 = scoreDetailService.insert(detail);
        if(!result3){
            throw new RuntimeException();
        }
        //发送消息修改播放列表内容
        JSONObject data = new JSONObject();
        data.put("writeType", WriteType.UPDATE_FILM.getType());
        rabbitTemplate.convertAndSend(RabbitConstant.DY_EXCHANGE_KEY,RabbitConstant.WRITE_KEY,data.toString().getBytes());

    }

}

