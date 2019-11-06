package com.ricky.danmu.consumer;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ricky.common.constant.RabbitConstant;
import com.ricky.common.enums.WriteType;
import com.ricky.common.utils.WriteFileUtils;
import com.ricky.danmu.po.Film;
import com.ricky.danmu.service.FilmService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description: 用于回复弹幕操作的监听器，当接收到指令后，修改文件中的内容
 * 直播软件可以监听文件的内容并投射到屏幕中展示
 * 1.修改播放信息列表的内容
 * 2.修改用户查看积分情况的内容
 * 3.修改用户送礼物记录的内容
 *
 * @Author: ricky
 * @Date: 2019/7/10 10:42
 */
@Component
public class WriteListener {
    /**
     *  播放信息列表的内容的文件
     */
    private static final String FILM_PATH = "C:\\Users\\Administrator\\Desktop\\ploy\\a.txt";
    /**
     *  用户查看积分情况的内容的文件
     */
    private static final String USER_SCORE_PATH = "C:\\Users\\Administrator\\Desktop\\ploy\\b.txt";
    /**
     *  用户送礼物记录的内容的文件
     */
    private static final String GIFT_RECORD_PATH = "C:\\Users\\Administrator\\Desktop\\ploy\\c.txt";
    @Autowired
    private FilmService filmService;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.WRITE_QUERE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DY_EXCHANGE_KEY, type = ExchangeTypes.TOPIC),
                    key = RabbitConstant.WRITE_KEY)
    )
    public void writeFile(byte[] message) {
        String jsonStr = new String(message);
        JSONObject data = JSONObject.parseObject(jsonStr);
        Integer writeType = data.getInteger("writeType");

        if(null == writeType){
            return;
        }
        if(WriteType.UPDATE_FILM.getType()==writeType){
            updateFilm();
        }else if(WriteType.UPDATE_USER_SCORE.getType()==writeType){
            String content = data.getString("content");
            if(!StringUtils.isEmpty(content)){
                updateUserScore(content);
            }
        }else if(WriteType.UPDATE_GIFT_RECORD.getType()==writeType){
            String content = data.getString("content");
            if(!StringUtils.isEmpty(content)){
                updateGiftRecord(content);
            }
        }else {
            System.out.println(data.toJSONString());
        }

    }

    /**
     * 更新电影排名
     */
    private void updateFilm(){
        StringBuilder sb = new StringBuilder();
        //正在播放中的电影
        Film playingFilm = filmService.selectOne(new EntityWrapper<Film>().eq("status", 1));
        sb.append("正在播放："+playingFilm.getName()+"\r\n");
        Page<Film> filmPage = filmService.selectPage(
                new Page<>(1, 15),
                new EntityWrapper<Film>()
                        .eq("status", 0)
                        .orderBy("score desc,update_time"));
        List<Film> records = filmPage.getRecords();
        int i = 1;
        for(Film film:records){
            //1.X战警（500）
            sb.append(i).append("."+film.getName()+"("+film.getScore()+")"+"\r\n");
            i++;
        }
        String path = FILM_PATH;
        WriteFileUtils.writeDataToFile(path,sb.toString());
    }

    /**
     * 更新用户的分数，用户查询积分的时候使用
     */
    private void updateUserScore(String content){
        try {
            //休眠3秒
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String path = USER_SCORE_PATH;
        WriteFileUtils.writeDataToFile(path,content);
    }

    /**
     * 更新赠送礼物记录
     */
    private void updateGiftRecord(String content){
        try {
            //休眠5秒
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String path = GIFT_RECORD_PATH;
        WriteFileUtils.writeDataToFile(path,content);
    }
}
