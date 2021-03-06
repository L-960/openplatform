# openplatform

>技术栈：ssm+springboot+springcloud+mysql+rabbitmq+redis+。。。
> 

## 1.eureka模块
>eureka服务器，单机配置，项目完成后根据需要配置eureka的高可用，也就是集群  
- 端口:20000
- 注册中心地址:http://localhost:20000/eureka  
- username: admin
- 密码:admin
- 址进行放行:/eureka/**
## 2.config模块
>config server统一配置中心，为其他模块提供配置，基于gitee，提供基于rabbitmq的手动刷新。如有配置自动刷新的需求，则配置webhooks来post我们的公网ip
- 端口:12000
- git服务器仓库地址:https://gitee.com/l-960/config_resp
- 手动刷新配置服务器:rabbitmq
- 手动刷新地址：http://localhost:12000/actuator/bus-refresh
## 3.cache模块
> 使用redis缓存，单机，迫于服务器压力，项目完成后根据需求配置集群，重写redisTemplate序列化方式，编写redis服务接口，对内提供http服务，端口不对外开放，配置文件使用config，在gitee仓库中
- 端口：21000
- 开放的服务: 增删改查 hash增删改查 setnx 自增自减 
## 4.webmaster模块
> 整合ssm项目，改成微服务项目。普通的ssm项目，前端基于layui，后端mysql5.7x，使用feign来操作缓存
- 端口：8080
- 使用缓存的服务1：ApiMappingServiceImpl路由管理
- 使用缓存的服务2：ApiSystemparamServiceImpl系统参数管理
- 使用缓存的服务3：AppInfoServiceImpl应用管理
- 使用缓存的服务4：CustomerServiceImpl客户管理
- 。。。
## 4.gateway模块
> ***
>关闭对外所有服务，为保证能够接收请求，随便开放一个服务来接收请求，目的是保证我们的zuul可以使用。

> 在webmaster服务中上传我们开放的测试接口，测试接口一定要注册到eureka中，否则在网关中找不到其服务，然后在我们的网关中利用前置过滤器，
>根据前端传来的apiname查找redis缓存中开放的接口数据，查询成功后继续查询serviceId以及url，然后利用zuul进行服务的跳转，未查询到数据则返回错误信息

>
- 端口：31000
- SystemParamFilter 10 系统参数校验
- TimestampFilter 20 校验请求是否过期
- SignFilter 30 校验用户的请求中的签名
- IdempotentsFilter 40 幂等性校验
- RoutingFilter 100 转发对应服务
- ServiceParamsFiltr XX 校验服务需要的参数
