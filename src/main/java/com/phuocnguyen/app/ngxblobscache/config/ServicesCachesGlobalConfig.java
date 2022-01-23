package com.phuocnguyen.app.ngxblobscache.config;

import com.phuocnguyen.app.ngxblobssrv.service.NgxRedisBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxRedisBaseServiceImpl;
import com.sivaos.config.propertiesConfig.RedisPubSubProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@SuppressWarnings("All")
@Configuration
public class ServicesCachesGlobalConfig {

    @Bean
    @Primary
    @Resource(name = "ngxRedisBaseService")
    public NgxRedisBaseService ngxRedisBaseService() {
        return new NgxRedisBaseServiceImpl();
    }

    @Bean
    @Primary
    public RedisPubSubProperties redisPubSubProperties() {
        return new RedisPubSubProperties();
    }
}
