package com.ricky.common.enums;

/**
  * @Description: 写文件类型
  * @author: ricky
  * @Date: 2018/11/29 10:52
  */
public enum WriteType {
    /**
     * 更新电影列表
     */
    UPDATE_FILM(10),
    /**
     * 更新用户积分情况
     */
    UPDATE_USER_SCORE(20),
    /**
     * 更新送礼物记录
     */
    UPDATE_GIFT_RECORD(30);


    private WriteType(Integer type){
        this.setType(type);
    }

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
