package com.lxy.openplatform.gateway.cache;


import com.lxy.openplatform.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 应用数据
 * 缓存作用
 */
@Component
public class InitAppKey {

    @Autowired
    private CacheService cacheService;

    public static final Map<String, Map<String, Object>> APP_KEY_MAP_CACHE = new ConcurrentHashMap<>();

    //这个注解是spring创建对象之后执行的
    @PostConstruct
    public void init() {

        try {

            //从redis中查询数据并保存到本地
            Set<String> allKeys = cacheService.findKeyByPartten("APPKEY:*");

            APP_KEY_MAP_CACHE.clear();

            System.err.println("redis发生变化,缓存更新------>redis中所有和应用相关信息的key:" + allKeys);

            //map,key：method的值,value对应的信息map
            if (allKeys != null) {
                for (String key : allKeys) {
                    Map<String, Object> ApiInfoMap = cacheService.hGetAll(key); //注意这个key已经包含了前缀
                    APP_KEY_MAP_CACHE.put(key, ApiInfoMap);//保存信息
                }
            }

            System.err.println(APP_KEY_MAP_CACHE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
