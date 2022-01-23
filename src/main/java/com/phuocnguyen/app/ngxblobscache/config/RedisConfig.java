package com.phuocnguyen.app.ngxblobscache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivaos.Utils.LoggerUtils;
import com.sivaos.config.propertiesConfig.RedisPubSubProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

/*
 * compile group: 'org.springframework.data', name: 'spring-data-redis', version: '2.3.4.RELEASE'
 * compile group: 'redis.clients', name: 'jedis', version: '3.1.0'
 * implementation group: 'io.lettuce', name: 'lettuce-core', version: '5.1.7.RELEASE'
 * */

@SuppressWarnings("All")
@Configuration
@EnableCaching
@EnableRedisRepositories
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

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

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();

        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        ObjectMapper mapper = new ObjectMapper(jsonFactory);

        objectRedisTemplate.setConnectionFactory(jeDisConnectionFactory());
        objectRedisTemplate.setKeySerializer(stringSerializer);
        objectRedisTemplate.setHashKeySerializer(stringSerializer);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        objectRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        objectRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        objectRedisTemplate.setEnableTransactionSupport(true);
        objectRedisTemplate.afterPropertiesSet();
        return objectRedisTemplate;
    }
    /* 1. end#Popularity */

    @PreDestroy
    public void cleanRedis() {
        jeDisConnectionFactory().getConnection().flushDb();
    }

    /* 2. start#Advenced */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        LettuceConnectionFactory redisConnectionFactory = jeDisConnectionFactory();
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }
    /* 2. end#Advenced */
}
