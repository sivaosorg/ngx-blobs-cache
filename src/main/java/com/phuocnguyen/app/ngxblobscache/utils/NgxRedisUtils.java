package com.phuocnguyen.app.ngxblobscache.utils;

import com.ngxsivaos.model.request.RedisStylesRequest;
import com.ngxsivaos.utils.RedisStylesUtils;
import com.sivaos.Utility.StringUtility;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ConstantConditions", "rawtypes", "All"})
public class NgxRedisUtils {

    public static Long increaseKey(RedisTemplate<String, Object> redisTemplate, String key) {
        if (StringUtility.isEmpty(key)) {
            return -1L;
        }

        return (long) redisTemplate.execute((RedisCallback) connection -> {
            byte[] paramBytes = redisTemplate.getStringSerializer().serialize(key);
            return connection.incr(paramBytes);
        }, true);
    }

    public static Long increaseKey(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key) {
        return increaseKey(redisTemplate, RedisStylesUtils.takeRedisKey(key));
    }

    public static Long decreaseKey(RedisTemplate<String, Object> redisTemplate, String key) {
        if (StringUtility.isEmpty(key)) {
            return -1L;
        }

        return (long) redisTemplate.execute((RedisCallback) connection -> {
            byte[] paramBytes = redisTemplate.getStringSerializer().serialize(key);
            return connection.decr(paramBytes);
        }, true);
    }

    public static Long decreaseKey(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key) {
        return decreaseKey(redisTemplate, RedisStylesUtils.takeRedisKey(key));
    }

    public static Long increaseKeyBy(RedisTemplate<String, Object> redisTemplate, String key, long value) {
        final String preKey = key;
        final long preValue = value;
        return (long) redisTemplate.execute(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) {
                byte[] paramBytes = redisTemplate.getStringSerializer().serialize(preKey);
                return connection.incrBy(paramBytes, preValue);
            }
        }, true);
    }

    public static Long increaseKeyBy(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long value) {
        return increaseKeyBy(redisTemplate, RedisStylesUtils.takeRedisKey(key), value);
    }

    public static Long decreaseKeyBy(RedisTemplate<String, Object> redisTemplate, String key, long value) {
        final String preKey = key;
        final long preValue = value;
        return (long) redisTemplate.execute(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) {
                byte[] paramBytes = redisTemplate.getStringSerializer().serialize(preKey);
                return connection.decrBy(paramBytes, preValue);
            }
        }, true);
    }

    public static Long decreaseKeyBy(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long value) {
        return decreaseKeyBy(redisTemplate, RedisStylesUtils.takeRedisKey(key), value);
    }

    public static Long increaseKeyEx(RedisTemplate<String, Object> redisTemplate, String key, long timeOut, TimeUnit unit) {
        long value = increaseKey(redisTemplate, key);
        redisTemplate.expire(key, timeOut, unit);
        return value;
    }

    public static Long increaseKeyEx(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long timeOut, TimeUnit unit) {
        return increaseKeyEx(redisTemplate, RedisStylesUtils.takeRedisKey(key), timeOut, unit);
    }

    public static Long decreaseKeyEx(RedisTemplate<String, Object> redisTemplate, String key, long timeOut, TimeUnit unit) {
        long value = decreaseKey(redisTemplate, key);
        redisTemplate.expire(key, timeOut, unit);
        return value;
    }

    public static Long decreaseKeyEx(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long timeOut, TimeUnit unit) {
        return decreaseKeyEx(redisTemplate, RedisStylesUtils.takeRedisKey(key), timeOut, unit);
    }

    public static Long increaseKeyByEx(RedisTemplate<String, Object> redisTemplate, String key, long value, long timeOut, TimeUnit unit) {
        long keySet = increaseKeyBy(redisTemplate, key, value);
        redisTemplate.expire(key, timeOut, unit);
        return keySet;
    }

    public static Long increaseKeyByEx(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long value, long timeOut, TimeUnit unit) {
        return increaseKeyByEx(redisTemplate, RedisStylesUtils.takeRedisKey(key), value, timeOut, unit);
    }

    public static Long decreaseKeyByEx(RedisTemplate<String, Object> redisTemplate, String key, long value, long timeOut, TimeUnit unit) {
        long keySet = decreaseKeyBy(redisTemplate, key, value);
        redisTemplate.expire(key, timeOut, unit);
        return keySet;
    }

    public static Long decreaseKeyByEx(RedisTemplate<String, Object> redisTemplate, RedisStylesRequest key, long value, long timeOut, TimeUnit unit) {
        return decreaseKeyByEx(redisTemplate, RedisStylesUtils.takeRedisKey(key), value, timeOut, unit);
    }
}
