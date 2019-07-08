package com.ricky.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.ricky.common.enums.OperationType;

/**
 * @Description: 弹幕命令过滤
 * @Author: ricky
 * @Date: 2019/7/8 10:28
 */
public class DanmuUtils {
    public static JSONObject validate(String text){
        String firstStr = text.substring(0, 1);
        if("#".equals(firstStr)){
            return validateStr1(text);
        }else if("@".equals(firstStr)){
            return validateStr2(text);
        }else {
            return null;
        }
    }

    private static JSONObject validateStr1(String text){
        JSONObject data = new JSONObject();
        String order = text.substring(1, text.length());
        if("查询".equals(order)){
            data.put("order", OperationType.QueryScore.getType());
        }else if("打卡".equals(order)){
            data.put("order", OperationType.Login.getType());
        }
        return data;
    }

    private static JSONObject validateStr2(String text){
        JSONObject data = new JSONObject();
        int startIndex = text.indexOf("@",0);
        if(startIndex !=0){
            return null;
        }
        int endIndex = text.indexOf("-",0);
        if(endIndex == -1){
            return null;
        }
        String movieName = text.substring(1,endIndex);
        if("".equals(movieName)){
            return null;
        }
        String score = text.substring(endIndex+1,text.length());
        if("".equals(score)){
            return null;
        }
        Double scoreNum;
        try{
            scoreNum = Double.valueOf(score);
        }catch (Exception e){
            return null;
        }
        data.put("movieName",movieName);
        data.put("score",score);
        data.put("order",OperationType.PlayMovie.getType());
        return data;
    }
}
