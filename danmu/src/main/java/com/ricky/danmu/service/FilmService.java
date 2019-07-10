
package com.ricky.danmu.service;

import com.baomidou.mybatisplus.service.IService;
import com.ricky.danmu.po.Film;
import org.apache.ibatis.annotations.Param;

public interface FilmService extends IService<Film> {

    /**
     * 点播电影
     */
    void playMovie(String uid,String username, String movieName, Integer score);

}

