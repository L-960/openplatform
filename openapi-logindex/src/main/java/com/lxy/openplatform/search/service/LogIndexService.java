package com.lxy.openplatform.search.service;

import java.util.List;
import java.util.Map;

public interface LogIndexService {

    /**
     * 创建index和type
     * @param index
     * @param type
     * @throws Exception
     */
    void createIndexAndType(String index, String type) throws Exception;

    /**
     * 判断index是否存在
     * @param index
     * @return
     * @throws Exception
     */
    boolean existIndex(String index) throws Exception;

    /**
     * 添加数据
     * @param json
     * @throws Exception
     */
    void add(String json) throws Exception;

    /**
     * 查询数据
     * @param params 参数的json字符串
     * @throws Exception
     */
    List<Map> search(String params) throws Exception;

    /**
     * 查询数量
     * @param params
     * @throws Exception
     */
    long count(String params) throws Exception;

    /**
     * 计算某个时间范围内的每个接口的请求的平均时间
     * @param receiveStartTime 查询的时间范围的起点,指的是我们服务的发起时间
     * @param receiveStopTime 查询的终点时间,也是服务的发起时间
     * @throws Exception
     */
    Map<String, Integer> statAvg(String receiveStartTime, String receiveStopTime ) throws Exception;

}
