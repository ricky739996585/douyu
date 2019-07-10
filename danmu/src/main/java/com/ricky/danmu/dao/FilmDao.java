
package com.ricky.danmu.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ricky.danmu.po.Film;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmDao extends BaseMapper<Film> {

    /**
     * 更新电影点播积分
     * @param score 使用分数
     * @param name  电影名称
     */
    Integer updateDemand(@Param("score") Integer score,@Param("name")String name);

    /**
     * 更新正在播放的电影
     */
    Integer updatePlayingFilm();

    /**
     * 把积分最高的电影修改为正在播放的状态
     */
    Integer changeFilmToPlaying();

}

