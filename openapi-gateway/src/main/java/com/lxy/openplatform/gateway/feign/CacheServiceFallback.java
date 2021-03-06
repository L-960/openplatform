package com.lxy.openplatform.gateway.feign;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * feign的降级机制,具体实现有idea的时候再补充
 */
@Component
public class CacheServiceFallback implements CacheService {
    @Override
    public boolean save2Redis(String key, String value, long expireTime) throws Exception {
        return false;
    }

    @Override
    public String getFromRedis(String key) throws Exception {
        return null;
    }

    @Override
    public boolean deleteKey(String key) throws Exception {
        return false;
    }

    @Override
    public boolean expire(String key, long expireTime) throws Exception {
        return false;
    }

    @Override
    public Long getAutoIncrementId(String key) throws Exception {
        return null;
    }

    @Override
    public Set<Object> sMembers(String key) throws Exception {
        return null;
    }

    @Override
    public Long sAdd(String key, String member) throws Exception {
        return null;
    }

    @Override
    public Long sAdd(String key, String[] members) throws Exception {
        return null;
    }

    @Override
    public Long sRemove(String key, String member) throws Exception {
        return null;
    }

    @Override
    public boolean hSet(String key, String field, String value) throws Exception {
        return false;
    }

    @Override
    public String hGet(String key, String field) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> hGetAll(String key) throws Exception {
        return null;
    }

    @Override
    public boolean hMSet(String key, Map<Object, Object> values) throws Exception {
        return false;
    }

    @Override
    public Set<String> findKeyByPartten(String partten) throws Exception {
        return null;
    }

    @Override
    public Long getAutoIncrementId(String key, long delta) throws Exception {
        return null;
    }

    @Override
    public Long hIncrementId(String key, String field, long delta) throws Exception {
        return null;
    }

    @Override
    public boolean setNx(String key, String value, long expireTime) throws Exception {
        return false;
    }

    @Override
    public boolean hMSet(String key, Object value) throws Exception {
        return false;
    }
}
