package com.lxy.openapi.order.service;

import com.lxy.openapi.order.pojo.PopOrderData;

import java.util.Map;

public interface PopOrderDataService {

    //根据主键查询订单信息
    public Map<String,Object> findByOrderId(Long id);

    public int updateStatus(PopOrderData pojo, String exp) ;
}
