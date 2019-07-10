
package com.ricky.player.po;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("score_detail")
@Data
@EqualsAndHashCode(callSuper = false)
public class ScoreDetail extends Model<ScoreDetail> implements Serializable {

    /**
     * 积分详情表
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 第三方用户ID
     */
    private String uuid;

    /**
     * 操作类型（点播：10,查询积分：20,打卡：30）
     */
    private Integer operationType;
    /**
     * 产生积分数
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

