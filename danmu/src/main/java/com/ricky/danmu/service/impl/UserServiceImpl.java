
package com.ricky.danmu.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ricky.danmu.dao.UserDao;
import com.ricky.danmu.po.User;
import com.ricky.danmu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}

