
package com.ricky.danmu.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.danmu.dao.FilmDao;
import com.ricky.danmu.po.Film;
import com.ricky.danmu.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class FilmServiceImpl extends ServiceImpl<FilmDao, Film> implements FilmService {
}

