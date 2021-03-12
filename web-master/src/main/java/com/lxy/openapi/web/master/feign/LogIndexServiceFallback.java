package com.lxy.openapi.web.master.feign;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * r4日志服务的熔断
 */
@Component
public class LogIndexServiceFallback implements LogIndexService {

    @Override
    public List<Map> searchLog(String paras) {
        return null;
    }

    @Override
    public Long searchLogCount(String paras) {
        return 0L;
    }
	
	@Override
    public int createindex() {
        return 0;
    }

    @Override
    public Map<String, Integer> statApiCountAndAvg( String startTime,String endTime) {
        return new HashMap<>();
    }
}
