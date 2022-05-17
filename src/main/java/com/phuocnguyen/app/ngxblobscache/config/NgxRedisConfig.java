package com.phuocnguyen.app.ngxblobscache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivaos.Utils.LoggerUtils;
import com.sivaos.Utils.ObjectUtils;
import com.sivaos.config.propertiesConfig.RedisPubSubProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

/*
 * compile group: 'org.springframework.data', name: 'spring-data-redis', version: '2.3.4.RELEASE'
 * compile group: 'redis.clients', name: 'jedis', version: '3.1.0'
 * implementation group: 'io.lettuce', name: 'lettuce-core', version: '5.1.7.RELEASE'
 * implementation group: 'org.springframework.integration', name: 'spring-integration-redis', version: '5.5.11'
 * implementation group: 'org.springframework.boot', name: 'spring-boot-starter-integration', version: '2.6.7'
 * */

@SuppressWarnings("All")
@Configuration
@EnableCaching
@EnableRedisRepositories
public class NgxRedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(NgxRedisConfig.class);

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private RedisPubSubProperties redisPubSubProperties;

    @Value("${spring.redis.host:localhost}")
    private String HOST;

    @Value("${spring.redis.password:123456}")
    private String PASSWORD;

    @Value("${spring.redis.port:6397}")
    private int PORT;

    @Value("${spring.redis.jedis.pool.max-active:100}")
    private int MAX_ACTIVE;

    @Value("${spring.redis.jedis.pool.max-idle:100}")
    private int MAX_IDLE;

    @Value("${spring.redis.jedis.pool.min-idle:10}")
    private int MIN_IDLE;


    @PostConstruct
    private void initializeProps() {
        if (logger.isInfoEnabled()) {
            logger.info("redisProperties- {}", LoggerUtils.toJson(redisProperties));
            logger.info("redisPubSubProperties- {}", LoggerUtils.toJson(redisPubSubProperties));
        }
    }

    /* 1. start#Popularity */
    @Bean
    @ConditionalOnMissingBean(name = "poolConfig")
    public JedisPoolConfig poolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        jedisPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getJedis().getPool().getMinIdle());
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        jedisPoolConfig.setNumTestsPerEvictionRun(3);
        jedisPoolConfig.setBlockWhenExhausted(true);
        return jedisPoolConfig;
    }

    @Bean
    @ConditionalOnMissingBean(name = "jedisPool")
    public JedisPool jedisPool(JedisPoolConfig poolConfig) {
        return new JedisPool(poolConfig,
                redisProperties.getHost(),
                redisProperties.getPort(),
                ObjectUtils.allNotNull(redisProperties.getTimeout()) ?
                        redisProperties.getTimeout().getNano() : 20000,
                redisProperties.getPassword());
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return configuration;
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolingClientConfiguration() {
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig())
                .commandTimeout(Duration.ofSeconds(100))
                .build();
    }

    @Bean
    public LettuceConnectionFactory jeDisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisStandaloneConfiguration(), lettucePoolingClientConfiguration());
        lettuceConnectionFactory.setShareNativeConnection(true);
        return lettuceConnectionFactory;
    }

    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

        ObjectMapper mapper = new ObjectMapper(jsonFactory);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        return jackson2JsonRedisSerializer;
    }

    private RedisSerializer<String> redisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = this.jackson2JsonRedisSerializer();
        RedisSerializer<String> serializer = this.redisSerializer();
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(jeDisConnectionFactory());
        template.setKeySerializer(serializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
    /* 1. end#Popularity */

    @PreDestroy
    public void cleanRedis() {
        jeDisConnectionFactory().getConnection().flushDb();
    }

    /* 2. start#Advenced */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = this.jackson2JsonRedisSerializer();
        LettuceConnectionFactory redisConnectionFactory = jeDisConnectionFactory();
        RedisSerializer<String> serializer = this.redisSerializer();

        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);

        template.setKeySerializer(serializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
    /* 2. end#Advenced */


    /* 3. start#CacheManager */
    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        LettuceConnectionFactory redisConnectionFactory = jeDisConnectionFactory();

        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
    /* 3. end#CacheManager */

    /* 4. start#JedisClient */
    @Bean
    public Jedis jedis(@Qualifier("jedisPool") JedisPool jedisPool) {
        return jedisPool.getResource();
    }
    /* 4. end#JedisClient */

    /* 5. start#Locks */
    @Bean
    public RedisLockRegistry redisLockRegistry() {
        return new RedisLockRegistry(jeDisConnectionFactory(), "redis_locks", 6000L);
    }
    /* 5. end#Locks */
}
