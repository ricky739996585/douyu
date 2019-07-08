package com.ricky.common.enums;

/**
  * @Description: 通知类型
  * @author: ricky
  * @Date: 2018/11/29 10:52
  */
public enum OperationType {
    /**
     * 点播电影
     */
    PlayMovie(10),
    /**
     * 查询积分
     */
    QueryScore(20),
    /**
     * 打卡
     */
    Login(30),;


    private OperationType(Integer type){
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
