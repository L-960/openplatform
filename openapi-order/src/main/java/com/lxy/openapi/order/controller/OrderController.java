package com.lxy.openapi.order.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxy.openapi.order.bean.OrderCancelBean;
import com.lxy.openapi.order.bean.OrderGetBean;
import com.lxy.openapi.order.pojo.PopOrderData;
import com.lxy.openapi.order.service.PopOrderDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
* 测试controller
* */
@RestController
public class OrderController {

    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private PopOrderDataService popOrderDataService;

    @PostMapping(value = "/ordercancel")
    public String can(@RequestBody() OrderCancelBean jsonObject) {
        /*
        * exp = 1 是自定义的异常 模仿分布式环境下的事务失效
        * */

        String order_id1 = jsonObject.getOrder_id();

        Long order_id = Long.parseLong(order_id1);

        String exp =  jsonObject.getExp();

        Map<String, Object> map = new HashMap<>();

        PopOrderData popOrderData = new PopOrderData();

        popOrderData.setId(order_id);

        popOrderData.setState(6L);

        int i = 0;
        try {
            i = popOrderDataService.updateStatus(popOrderData, exp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (i >= 2) {
            map.put("code", "ok");
            map.put("msg", "退货成功");
        } else {
            map.put("code", "-1");
            map.put("msg", "退货失败");
        }
        String s = JSON.toJSONString(map);
        return s;
    }

    @PostMapping("/orderget")
    public String findById(@RequestBody OrderGetBean param_json) {

        //JSONObject jsonObject = JSON.parseObject(param_json);
        //String order_id1 = (String) jsonObject.get("order_id");
        //Long order_id = Long.parseLong(order_id1);
        //Object order_state = jsonObject.get("order_state");

        String order_id1 = param_json.getOrder_id();

        Long order_id = Long.parseLong(order_id1);

        Map<String, Object> byId = popOrderDataService.findByOrderId(order_id);
        String s = JSON.toJSONString(byId);
        return s;
    }


}
