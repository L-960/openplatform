package com.lxy.openplatform.search.listener;


import com.lxy.openplatform.search.service.LogIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class GateWayLogListener {

    @Autowired
    private LogIndexService logIndexService;

    @StreamListener("GateWay->LogIndexChange")
    public void onMessage(String data) {

        System.err.println("收到的数据是:" + data);

        try {
            // 执行es存储业务
            logIndexService.add(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
