package com.lxy.openplatform.author.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * @Author Lvxy
 * @Desc
 */
@FeignClient(value ="OPENAPI-CACHE" ,fallback = CacheServiceFallback.class)
public interface CacheService {

    @PostMapping("/cache/set/{key}/{value}/{expireTime}")
    boolean save2Redis(@PathVariable String key, @PathVariable String value, @PathVariable long expireTime) throws Exception ;

    @GetMapping("/cache/get/{key}")

    String getFromRedis(@PathVariable String key) throws Exception;

    @PostMapping("/cache/delete/{key}")
    boolean deleteKey(@PathVariable String key) throws Exception ;

    @PostMapping("/cache/expire/{key}/{expireTime}")
    boolean expire(@PathVariable String key, @PathVariable long expireTime) throws Exception;

    @GetMapping("/cache/getid/{key}")
    Long getAutoIncrementId(@PathVariable String key) throws Exception;

    @GetMapping("/cache/smembers/{key}")
    Set<Object> sMembers(@PathVariable String key) throws Exception ;

    @PostMapping("/cache/sadd/{key}/{member}")
    Long sAdd(@PathVariable String key, @PathVariable String member) throws Exception ;

    @PostMapping("/cache/sadds/{key}")
    Long sAdd(@PathVariable String key, @RequestParam("members") String[] members) throws Exception;

    @PostMapping("/cache/sremove/{key}/{member}")
    Long sRemove(@PathVariable String key, @PathVariable String member) throws Exception ;


    @PostMapping("/cache/hset/{key}/{field}/{value}")
    boolean hSet(@PathVariable String key, @PathVariable String field, @PathVariable String value) throws Exception ;

    @GetMapping("/cache/hget/{key}/{field}")
    String hGet(@PathVariable String key, @PathVariable String field) throws Exception;


    @GetMapping("/cache/hgetall/{key}")
    Map<Object, Object> hGetAll(@PathVariable String key) throws Exception;

    @PostMapping("/cache/hmset/{key}")
    boolean hMSet(@PathVariable String key, @RequestBody Map<Object, Object> values) throws Exception ;


    @GetMapping("/cache/keys/{partten}")
    Set<String> findKeyByPartten(@PathVariable String partten) throws Exception ;

    @GetMapping("/cache/increment/{key}/{delta}")
    Long getAutoIncrementId(@PathVariable String key, @PathVariable long delta) throws Exception;

    @GetMapping("/cache/hIncrementid/{key}/{field}/{delta}")
    Long hIncrementId(@PathVariable String key, @PathVariable String field, @PathVariable long delta) throws Exception;

    @PostMapping("/cache/setnx/{key}/{value}/{expireTime}")
    boolean setNx(@PathVariable String key, @PathVariable String value, @PathVariable long expireTime) throws Exception;

    /**
     * 这个方法和上面的hMSet是一样的作用,都是将参数转成json写出去
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @PostMapping("/cache/hmset/{key}")
    boolean hMSet(@PathVariable String key, @RequestBody Object value) throws Exception ;


}

