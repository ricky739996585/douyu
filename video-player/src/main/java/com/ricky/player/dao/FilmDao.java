
package com.ricky.player.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ricky.player.po.Film;
import com.ricky.player.po.User;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmDao extends BaseMapper<Film> {

}

