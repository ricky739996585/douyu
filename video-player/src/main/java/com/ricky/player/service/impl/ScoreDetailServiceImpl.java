
package com.ricky.player.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.player.dao.ScoreDetailDao;
import com.ricky.player.po.ScoreDetail;
import com.ricky.player.service.ScoreDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class ScoreDetailServiceImpl extends ServiceImpl<ScoreDetailDao, ScoreDetail> implements ScoreDetailService {
}

