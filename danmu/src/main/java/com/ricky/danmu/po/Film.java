
package com.ricky.danmu.po;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("film")
@Data
@EqualsAndHashCode(callSuper = false)
public class Film extends Model<Film> implements Serializable {

    /**
     * 电影信息表
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 积分数
     */
    private Integer score;
    /**
     * 播放链接
     */
    private String url;
    /**
     * 播放次数
     */
    private Integer playNum;
    /**
     * 点播次数
     */
    private Integer demandNum;

    /**
     * 版本号
     */

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

