
package com.ricky.danmu.po;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("user")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends Model<User> implements Serializable {

    /**
     * 用户表
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private String uuid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户积分数
     */
    private Integer score;

    /**
     * 版本号
     */

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

