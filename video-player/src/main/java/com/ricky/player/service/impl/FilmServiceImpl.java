
package com.ricky.player.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.player.dao.FilmDao;
import com.ricky.player.dao.UserDao;
import com.ricky.player.po.Film;
import com.ricky.player.po.User;
import com.ricky.player.service.FilmService;
import com.ricky.player.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class FilmServiceImpl extends ServiceImpl<FilmDao, Film> implements FilmService {
}

