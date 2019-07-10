
package com.ricky.player.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ricky.player.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends BaseMapper<User> {
    /**
     * 减少用户积分
     * @param score 积分数
     * @param uuid  用户ID
     */
    Integer updateCutScore(@Param("score") Integer score, @Param("uuid") String uuid);

    /**
     * 增加用户积分
     * @param score 积分数
     * @param uuid  用户ID
     */
    Integer updateAddScore(@Param("score") Integer score, @Param("uuid") String uuid);
}

