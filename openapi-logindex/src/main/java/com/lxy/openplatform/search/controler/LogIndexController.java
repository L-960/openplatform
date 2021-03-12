package com.lxy.openplatform.search.controler;


import com.lxy.openplatform.search.service.LogIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/log")
public class LogIndexController {

    @Value("${spring.data.elasticsearch.index}")
    private String index;

    @Value("${spring.data.elasticsearch.type}")
    private String type;

    @Autowired
    private LogIndexService logIndexService;

    @RequestMapping("/createindex")
    public String createIndex() {

        try {
            logIndexService.createIndexAndType(index, type);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fail";
    }

    @RequestMapping("/searchlog")
    public List<Map> searchLog(String paras) {

        try {
            return logIndexService.search(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    @RequestMapping("/searchcount")
    public Long searchCount(String paras) {

        try {
            return logIndexService.count(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0L;
    }


    @RequestMapping("/avg")
    public  Map<String, Integer> getAvg (String receiveStartTime, String receiveStopTime) {

        try {
            return logIndexService.statAvg(receiveStartTime,receiveStopTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }
}
