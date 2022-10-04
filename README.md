<h1 align="center">
  <img alt="Eagle logo" src="assets/cache.png" width="224px"/><br/>
  Config middleware base caches
</h1>

<p align="center">
The base library including redis cache, ...
<br/>
</p>


## ⚡️ Quick start

Build application:

```bash
/bin/bash gradlew jar
```
> output jar: <b><i>ngx-blobs-cache-1.0.0.jar</i></b>

## :rocket: Functions

#### Redis Cache Attributes

:package: add file [`application-redis.yml`](src/main/resources/application-redis.yml)

```yml
spring:
  redis:
    database: 0 # Redis database index (default is 0)
    jedis:
      pool:
        max-active: 20 # Maximum connection number (no limit is limited)
        max-wait: -1 # Connect the pool maximum blocking waiting time (no limit is limited)
        max-idle: 100 # Connect the maximum idle connection in the pool
        min-idle: 10
    timeout: 20000 # Connect timeout (s)
```

:package: checkout file [`application-params.yml`](src/main/resources/application-params.yml)

```yml
# ////////////////////////////
# Config Redis Jedis Attributes
# ////////////////////////////
---
spring:
  redis:
    jedis-starter:
      enabled: false # enable jedis client
```

:package: checkout `application-dev.yml`, `application-local.yml`, `application-prod.yml`

```yml
# ////////////////////////////
# Config Spring Attributes
# ////////////////////////////
---
spring:
  redis:
    host: 127.0.0.1
    password: 123456
    port: 6379
    sentinel:
      master:
        name: master_node
      host-and-port: 13.232.155.79:26379;13.232.155.88:26379;13.232.154.78:26379
```

#### Redis Pub-Sub Attributes

:package: checkout file [`application-params.yml`](src/main/resources/application-params.yml)

```yml
# ////////////////////////////
# Config hook redis pub-sub
# ////////////////////////////
---
spring:
  redis:
    pub-sub-starter:
      topic: CALLBACK # including: CALLBACK, MEMO, SHIFT, EFFECTOR and DEFAULT
      clusters:
        - enable: true
          order: 1 # order priority
          label: sys::props
          description: This is Sys props state
          clusterType: CALLBACK # including: CALLBACK, MEMO, SHIFT, EFFECTOR and DEFAULT
        - enable: true
          order: 2
          label: subscription
          description: This is Subscription props state
          clusterType: CALLBACK
          prefix: ::state
```

#### Services

- `NgxRedisBaseService`: use service for base action on redis

```java
/**
 * @apiNote RedisKeys - using this class to get keys type, ex: RedisKeys.getSysConfigKey("test")
 */
public interface NgxRedisBaseService {

    /**
     * Get list of basic objects of cache
     *
     * @param pattern string prefix
     * @return object list
     */
    Collection<String> keys(RedisTemplate<String, Object> redisTemplate, String pattern);

    /**
     * Cache basic objects, integer, string, physical classes, etc.
     *
     * @param key   Cache key value
     * @param value Cache
     */
    <T> void setCacheObject(RedisTemplate<String, Object> redisTemplate, String key, T value);

    /**
     * Cache basic objects, integer, string, physical classes, etc.
     *
     * @param key      Cache key value
     * @param value    Cache
     * @param timeout  time
     * @param timeUnit Time Parts
     */
    <T> void setCacheObject(RedisTemplate<String, Object> redisTemplate, String key, T value, Integer timeout, TimeUnit timeUnit);

    /**
     * Set effective time
     *
     * @param key     Redis button
     * @param timeout timeout time
     * @return true = set success; false = set failed
     */
    boolean expire(RedisTemplate<String, Object> redisTemplate, String key, long timeout);

    /**
     * Set effective time
     *
     * @param key     Redis button
     * @param timeout timeout time
     * @param unit    Time Unit
     * @return true = set success; false = set failed
     */

    boolean expire(RedisTemplate<String, Object> redisTemplate, String key, long timeout, TimeUnit unit);


    /**
     * Get the basic object of the cache.
     *
     * @param key Cache key value
     * @return Cache key value corresponding to data
     */

    <T> T getCacheObject(RedisTemplate<String, Object> redisTemplate, String key);

    /**
     * Delete a single object
     *
     * @param key -
     */
    boolean deleteObject(RedisTemplate<String, Object> redisTemplate, String key);

    /**
     * Delete set objects
     *
     * @param collection multiple objects
     * @return integer
     */

    long deleteObject(RedisTemplate<String, Object> redisTemplate, Collection<String> collection);

    /**
     * Cache List data
     *
     * @param key  Cache key value
     * @param list worthy List data
     * @return object
     */
    <T> long setCacheList(RedisTemplate<String, Object> redisTemplate, String key, List<T> list);

    /**
     * Get the List object of the cache
     *
     * @param key Cache key value
     * @return Cache key value corresponding to data
     */
    <T> List<T> getCacheList(RedisTemplate<String, Object> redisTemplate, String key);

    /**
     * Cache SET
     *
     * @param key     Cache key value
     * @param dataSet Cache data
     * @return object of cache data
     */
    <T> BoundSetOperations<String, T> setCacheSet(RedisTemplate<String, Object> redisTemplate, String key, Set<T> dataSet);

    /**
     * Get a cache SET
     *
     * @param key -
     * @return -
     */
    <T> Set<T> getCacheSet(RedisTemplate<String, Object> redisTemplate, String key);

    /**
     * Cache MAP
     *
     * @param key -
     * @param map -
     */
    <T> void setCacheMap(RedisTemplate<String, Object> redisTemplate, String key, Map<String, T> map);

    /**
     * Get the MAP of the cache
     *
     * @param key -
     * @return -
     */
    Map<Object, Object> getCacheMap(RedisTemplate<String, Object> redisTemplate, String key);

    /**
     * Save data from Hash
     *
     * @param key   Redis button
     * @param hKey  Hash button
     * @param value Value
     */
    <T> void setCacheMapValue(RedisTemplate<String, Object> redisTemplate, String key, String hKey, T value);

    /**
     * Get data in hash object
     *
     * @param key  Redis button
     * @param hKey Hash button
     * @return Hash objects
     */
    <T> T getCacheMapValue(RedisTemplate<String, Object> redisTemplate, String key, String hKey);

    /**
     * Get data in multiple HASH
     *
     * @param key   Redis button
     * @param hKeys Hash key collection
     * @return hash object set
     */
    <T> List<T> getMultiCacheMapValue(RedisTemplate<String, Object> redisTemplate, String key, Collection<Object> hKeys);

    Collection<String> keys(RedisTemplate<String, Object> redisTemplate);

    boolean containsKey(RedisTemplate<String, Object> redisTemplate, String key);

    <T> void publishEvent(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, T data);

    <T> void publishEvent(RedisTemplate<String, Object> redisTemplate, RedisPubSubType topic, T data);

    <T> void publishEvent(RedisTemplate<String, Object> redisTemplate, RedisPubSubLabel topic, T data);

    Long countExistingKeys(RedisTemplate<String, Object> redisTemplate, Collection<String> keys);

    Boolean isAvailable(RedisTemplate<String, Object> redisTemplate);
}

```

- `NgxRedisStylesBaseService`: use service for all combine base `NgxRedisBaseService` for action on redis
  
```java
@SuppressWarnings({"UnusedReturnValue"})
public interface NgxRedisStylesBaseService {

    Collection<String> keys(String pattern);

    Collection<String> keys();

    <T> void setCacheObject(RedisStylesRequest redisStylesRequest, T value);

    <T> void setCacheObject(RedisStylesRequest redisStylesRequest, T value, Integer timeout, TimeUnit timeUnit);

    boolean expire(RedisStylesRequest redisStylesRequest, long timeout);

    boolean expire(RedisStylesRequest redisStylesRequest, long timeout, TimeUnit unit);

    <T> T getCacheObject(RedisStylesRequest redisStylesRequest);

    boolean deleteObject(RedisStylesRequest redisStylesRequest);

    long deleteObject(Collection<String> collection);

    <T> long setCacheList(RedisStylesRequest redisStylesRequest, List<T> list);

    <T> List<T> getCacheList(RedisStylesRequest redisStylesRequest);

    <T> BoundSetOperations<String, T> setCacheSet(RedisStylesRequest redisStylesRequest, Set<T> dataSet);

    <T> Set<T> getCacheSet(RedisStylesRequest redisStylesRequest);

    <T> void setCacheMap(RedisStylesRequest redisStylesRequest, Map<String, T> map);

    Map<Object, Object> getCacheMap(RedisStylesRequest redisStylesRequest);

    <T> void setCacheMapValue(RedisStylesRequest redisStylesRequest, String hKey, T value);

    <T> T getCacheMapValue(RedisStylesRequest redisStylesRequest, String hKey);

    <T> List<T> getMultiCacheMapValue(RedisStylesRequest redisStylesRequest, Collection<Object> hKeys);

    boolean containsKey(RedisStylesRequest redisStylesRequest);

    Long countExistingKeys(Collection<String> keys);

    <T> void publishEvent(ChannelTopic channelTopic, T data);

    <T> void publishEvent(RedisPubSubType topic, T data);

    <T> void publishEvent(RedisPubSubLabel topic, T data);

    SIVAResponseDTO<?> takeValuesFKeys(RedisStylesRequest redisStylesRequest);

    SIVAResponseDTO<?> takeValuesFKeys(String keyPref);

    <T> SIVAResponseDTO<?> updateCacheObject(String keyPref, T value);

    Boolean isAvailable();
}
```

- `NgxRedisUtils`: class utils for action on redis

```java
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
```

- `RedisStylesRequest`: model to build redis key
  
```java
    private static void onRedisKey() {

        RedisStylesRequest request = new RedisStylesRequest.RedisStylesRequestBuilder()
                .onMasterKey("parent_key")
                .onRedisKey("mouse_child")
                .onGeolocationType(GeolocationType.VIETNAM_GEOLOCATION)
                .onRedisPropsType(RedisPropsType.ObjectType)
                .onRedisStylesType(RedisStylesType.USER_KEY)
                .onUserKey("self")
                .build();

        System.out.println("redis key accomplished = " + request.asKey); // call field public to get redis key, o = parent_key:user:self:vietnam:object
        System.out.println("redis key accomplished = " + RedisStylesRequest.asKeyRendered(request)); // call from utils method to get redis key, o = parent_key:user:self:vietnam:object
    }
```