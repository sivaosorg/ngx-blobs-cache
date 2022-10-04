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


