package com.lxy.openplatform.gateway.cache;


import com.lxy.openplatform.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 路由管理数据 缓存作用
 * 缓存作用
 */
@Component
public class InitApiRouting {

    @Autowired
    private CacheService cacheService;

    public static final Map<String, Map<String, Object>> API_ROUTING_MAP_CACHE = new ConcurrentHashMap<>();

    //这个注解是spring创建对象之后执行的
    @PostConstruct
    public void init() {

        try {
            //从redis中查询数据并保存到本地
            Set<String> allKeys = cacheService.findKeyByPartten("APINAME:*");

            API_ROUTING_MAP_CACHE.clear();

            System.err.println("redis发生变化,缓存更新------>redis中所有和路由相关信息的key:" + allKeys);

            //map,key：method的值,value对应的信息map
            if (allKeys != null) {
                for (String key : allKeys) {
                    Map<String, Object> ApiInfoMap = cacheService.hGetAll(key);
                    //保存信息
                    API_ROUTING_MAP_CACHE.put(key, ApiInfoMap);
                }
            }

            System.err.println(API_ROUTING_MAP_CACHE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
