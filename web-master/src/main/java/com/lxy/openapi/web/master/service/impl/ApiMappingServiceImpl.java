package com.lxy.openapi.web.master.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lxy.openapi.web.master.mapper.ApiMappingMapper;
import com.lxy.openapi.web.master.mq.MqUtil;
import com.lxy.openapi.web.master.pojo.ApiMapping;
import com.lxy.openapi.web.master.service.ApiMappingService;
import com.lxy.openapi.web.master.feign.CacheService;
import com.lxy.openplatform.commons.APIRoutingType;
import com.lxy.openplatform.commons.constans.SystemParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApiMappingServiceImpl implements ApiMappingService {

    @Autowired
    private ApiMappingMapper apiMappingMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MqUtil mqUtil;


    @Override
    public void addApiMapping(ApiMapping mapping) {
        apiMappingMapper.addApiMapping(mapping);
        //添加到缓存
        try {
            //判断当前添加的数据是不是有效的数据
            if (mapping.getState() == 1) {
                cacheService.hMSet(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), mapping);
                //发送mq到网关
                mqUtil.sendMessage(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), APIRoutingType.ADD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateApiMapping(ApiMapping mapping) {
        apiMappingMapper.updateApiMapping(mapping);
        //更新分为以下情况,如果是原先是有效的数据,现在更新为无效的数据,则是从缓存中删除数据
        try {
            if (mapping.getState() == 1) {
                //如果更新的是其他内容(有效的) 则更新缓存中的数据
                cacheService.hMSet(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), mapping);
                //发送mq到网关
                mqUtil.sendMessage(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), APIRoutingType.UPDATE);
            }else{
                //从缓存中删除数据
                cacheService.deleteKey(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName());
                //发送mq到网关
                mqUtil.sendMessage(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), APIRoutingType.DELETE);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public PageInfo<ApiMapping> getMappingList(ApiMapping criteria, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(apiMappingMapper.getMappingList(criteria));
    }

    @Override
    public ApiMapping getMappingById(int id) {
        return apiMappingMapper.getMappingById(id);
    }

    @Override
    public void deleteMapping(int[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (int id : ids) {
            ApiMapping mapping = apiMappingMapper.getMappingById(id);
            if (mapping != null) {
                mapping.setState(0);
                apiMappingMapper.updateApiMapping(mapping);
                try {
                    //从缓存中删除数据
                    cacheService.deleteKey(SystemParams.METHOD_REDIS_PRE +mapping.getGatewayApiName());
                    //发送mq到网关
                    mqUtil.sendMessage(SystemParams.METHOD_REDIS_PRE + mapping.getGatewayApiName(), APIRoutingType.DELETE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
