package com.phuocnguyen.app.ngxblobscache.config;

import com.ngxsivaos.model.properties.JedisPoolProperties;
import com.phuocnguyen.app.ngxblobssrv.service.NgxRedisBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.NgxRedisStylesBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxRedisBaseServiceImpl;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxRedisStylesBaseServiceImpl;
import com.sivaos.config.propertiesConfig.RedisPubSubProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SuppressWarnings("All")
@Configuration
public class ServicesCachesGlobalConfig {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    @Primary
    @Resource(name = "ngxRedisBaseService")
    public NgxRedisBaseService ngxRedisBaseService() {
        return new NgxRedisBaseServiceImpl();
    }

    @Bean
    @Primary
    @Resource(name = "ngxRedisStylesBaseService")
    public NgxRedisStylesBaseService ngxRedisStylesBaseService() {
        return new NgxRedisStylesBaseServiceImpl(ngxRedisBaseService(), redisTemplate);
    }

    @Bean
    @Primary
    public RedisPubSubProperties redisPubSubProperties() {
        return new RedisPubSubProperties();
    }

    @Bean
    @Primary
    public JedisPoolProperties jedisPoolProperties() {
        return new JedisPoolProperties();
    }
}
