
package com.ricky.player.service;

import com.baomidou.mybatisplus.service.IService;
import com.ricky.player.po.User;

public interface UserService extends IService<User> {
    /**
     * 查询积分
     */
    void queryScore(String uid,String username);

    /**
     * 打卡获取积分
     */
    void loginSendScore(String uid,String username);
}

