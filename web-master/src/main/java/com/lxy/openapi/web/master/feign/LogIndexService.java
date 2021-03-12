package com.lxy.openapi.web.master.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "openapi-logindex", fallback = LogIndexServiceFallback.class)
public interface LogIndexService {

    @RequestMapping(value = "/log/searchlog", method = RequestMethod.POST)
    List<Map> searchLog(@RequestParam("paras") String paras);

    @RequestMapping(value = "/log/searchcount", method = RequestMethod.POST)
    Long searchLogCount(@RequestParam("paras") String paras);
	
	@RequestMapping(value = "/log/createindex", method = RequestMethod.POST)
    int createindex();

    @RequestMapping(value = "/log/avg", method = RequestMethod.POST)
    Map<String, Integer> statApiCountAndAvg(@RequestParam("receiveStartTime") String startTime, @RequestParam("receiveStopTime") String endTime);

}
