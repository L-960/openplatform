package com.lxy.openplatform.gateway.cache;


import com.lxy.openplatform.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.lxy.openplatform.commons.constans.SystemParams.SYSYTEMPARAMS;


/**
 * 系统参数
 * 缓存作用
 */
@Component
public class InitSystemParam {

    @Autowired
    private CacheService cacheService;

    public static final Set<Object> SYSTEM_PARAM_SET_CACHE = new HashSet<>();

    //这个注解是spring创建对象之后执行的
    @PostConstruct
    public void init() {

        try {

            //从redis中查询数据并保存到本地
            Set<Object> params = cacheService.sMembers(SYSYTEMPARAMS);

            SYSTEM_PARAM_SET_CACHE.clear();

            System.err.println("redis发生变化,缓存更新------>redis中所有和系统参数的key:" + params);

            //map,key：method的值,value对应的信息map
            if (params != null) {
                for (Object key : params) {
                    SYSTEM_PARAM_SET_CACHE.add(key);//保存信息
                }
            }

            System.err.println(SYSTEM_PARAM_SET_CACHE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
