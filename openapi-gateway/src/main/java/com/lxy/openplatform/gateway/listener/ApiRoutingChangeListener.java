package com.lxy.openplatform.gateway.listener;


import com.lxy.openplatform.commons.beans.ApiRoutingMQBean;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.gateway.cache.InitApiRouting;
import com.lxy.openplatform.gateway.cache.InitAppKey;
import com.lxy.openplatform.gateway.cache.InitSystemParam;
import com.lxy.openplatform.gateway.feign.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;


@Component
public class ApiRoutingChangeListener {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private InitAppKey initAppKey;

    @Autowired
    private InitApiRouting initApiRouting;

    @Autowired
    private InitSystemParam initSystemParam;

    // 这里监听消息 根据不同消息执行本地二级缓存的更新
    @StreamListener("WebMaster->GateWayChange")
    public void onMessage(ApiRoutingMQBean bean) {


        // 路由信息更新
        if (bean.getKey().startsWith(SystemParams.METHOD_REDIS_PRE)){
            initApiRouting.init();
//            try {
//                //从redis中查询数据并保存到本地
//                Set<String> allKeys = cacheService.findKeyByPartten("APINAME:*");
//
//                System.err.println("redis中所有和路由相关信息的key:" + allKeys);
//
//                //map,key：method的值,value对应的信息map
//                if (allKeys != null) {
//                    for (String key : allKeys) {
//                        Map<String, Object> ApiInfoMap = cacheService.hGetAll(key);
//                        //保存信息
//                        InitApiRouting.API_ROUTING_MAP_CACHE.put(key, ApiInfoMap);
//                    }
//                }
//
//                System.err.println(InitApiRouting.API_ROUTING_MAP_CACHE);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        // 应用信息更新
        if (bean.getKey().startsWith(SystemParams.APPKEY_REDIS_PRE )){
            initAppKey.init();
//            try {
//                //从redis中查询数据并保存到本地
//                Set<String> allKeys = cacheService.findKeyByPartten("APPKEY:*");
//
//                System.err.println("redis中所有和应用相关信息的key:" + allKeys);
//
//                //map,key：method的值,value对应的信息map
//                if (allKeys != null) {
//                    for (String key : allKeys) {
//                        Map<String, Object> ApiInfoMap = cacheService.hGetAll(key); //注意这个key已经包含了前缀
//                        InitAppKey.APP_KEY_MAP_CACHE.put(key, ApiInfoMap);//保存信息
//                    }
//                }
//
//                System.err.println(InitAppKey.APP_KEY_MAP_CACHE);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        // 系统参数信息更新
        if (bean.getKey().equals(SystemParams.SYSYTEMPARAMS)){
            initSystemParam.init();
//            try {
//
//                //从redis中查询数据并保存到本地
//                Set<Object> params = cacheService.sMembers(SystemParams.SYSYTEMPARAMS);
//
//                InitSystemParam.SYSTEM_PARAM_SET_CACHE.clear();
//
//                System.err.println("redis中所有和系统参数的key:" + params);
//
//                //map,key：method的值,value对应的信息map
//                if (params != null) {
//                    for (Object key : params) {
//                        InitSystemParam.SYSTEM_PARAM_SET_CACHE.add(key);//保存信息
//                    }
//                }
//
//                System.err.println(InitSystemParam.SYSTEM_PARAM_SET_CACHE);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        /*
        switch (bean.getType()) {
            case ADD:
            case UPDATE:
                try {
                    Map<String, Object> map = cacheService.hGetAll(bean.getKey());
                    InitApiRouting.API_ROUTING_MAP_CACHE.put(bean.getKey(), map);//保存起来
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case DELETE:
                InitApiRouting.API_ROUTING_MAP_CACHE.remove(bean.getKey());//从本地删除数据
                break;
        }
         */
    }
}
