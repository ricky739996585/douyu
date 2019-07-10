
package com.ricky.player.service;

import com.baomidou.mybatisplus.service.IService;
import com.ricky.player.po.Film;
import com.ricky.player.po.User;

public interface FilmService extends IService<Film> {
    /**
     * 点播电影
     */
    void playMovie(String uid,String username, String movieName, Integer score);
}

