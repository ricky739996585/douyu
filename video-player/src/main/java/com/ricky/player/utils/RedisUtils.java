/**
 * Copyright 2018 人人开源 http://www.renren.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ricky.player.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-07-17 21:12
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    @Resource(name="redisTemplate")
    private HashOperations<String, String, Object> hashOperations;
    @Resource(name="redisTemplate")
    private ListOperations<String, Object> listOperations;
    @Resource(name="redisTemplate")
    private SetOperations<String, Object> setOperations;
    @Resource(name="redisTemplate")
    private ZSetOperations<String, Object> zSetOperations;
    /**  默认过期时长，单位：秒 */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**  不设置过期时长 */
    public final static long NOT_EXPIRE = -1;

    public void set(String key, Object value, long expire){
        valueOperations.set(key, toJson(value));
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value){
        valueOperations.set(key, toJson(value));
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }

    //模糊删除key
    public Long fuzzyDelete(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        Long count = redisTemplate.delete(keys);
        return count;
    }
    //添加hash
    public void putAllHash(String key, Map<String, Object> map, long expire) {
        hashOperations.putAll(key, map);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }
    //获取hash
    public Map<String, Object> entries(String key) {
        return entries(key, NOT_EXPIRE);
    }
    //获取hash
    public Map<String, Object> entries(String key, long expire) {
        Map<String, Object> map = hashOperations.entries(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return map;
    }
    public Boolean hasHash(String key, Object object) {
        return hasHash(key, object, NOT_EXPIRE);
    }

    public Boolean hasHash(String key, Object object, long expire) {
        Boolean result = hashOperations.hasKey(key, object.toString());
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return result;
    }
    //模糊查询key
    public String fuzzyKey(String key) {
        Set<String> keySet = redisTemplate.keys(key + "*");
        Iterator iter = keySet.iterator();
        while (iter.hasNext()) {
            return iter.next().toString();
        }
        return "";
    }

    public Boolean zadd(String key, String value,Double score){
        return redisTemplate.opsForZSet().add(key,value,score);
    }

    public void zupdateAddScore(String key, String value,Double addScore){
        redisTemplate.opsForZSet().incrementScore(key,value,addScore);
    }

    public void zupdateDisScore(String key, String value,Double addScore){
        redisTemplate.opsForZSet().incrementScore(key,value,-addScore);
    }

    public Boolean zsetIsExist(String key, String value){
        Double score = redisTemplate.opsForZSet().score(key, value);
        return score != null;
    }

    public Set<ZSetOperations.TypedTuple<Object>> zsetList(String key){
        Set<ZSetOperations.TypedTuple<Object>> range = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        return range;
    }

//    public List<String> getSortList(String key){
//        SortQueryBuilder.sort(key).noSort().;
//    }

    public static void main(String[] args) {
        RedisUtils utils = new RedisUtils();

    }
}
