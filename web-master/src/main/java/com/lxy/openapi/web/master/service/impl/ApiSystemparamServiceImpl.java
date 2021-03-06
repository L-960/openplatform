package com.lxy.openapi.web.master.service.impl;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lxy.openapi.web.master.mapper.ApiSystemparamMapper;
import com.lxy.openapi.web.master.mq.MqUtil;
import com.lxy.openapi.web.master.pojo.ApiSystemparam;
import com.lxy.openapi.web.master.service.ApiSystemparamService;
import com.lxy.openapi.web.master.feign.CacheService;
import com.lxy.openplatform.commons.APIRoutingType;
import com.lxy.openplatform.commons.constans.SystemParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ApiSystemparamServiceImpl implements ApiSystemparamService {

    @Autowired
    private ApiSystemparamMapper systemparamDao;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MqUtil mqUtil;

    @Override
    public void addApiSystemparam(ApiSystemparam apiSystemparam) throws Exception {
        systemparamDao.insertApiSystemparam(apiSystemparam);
        cacheService.sAdd(SystemParams.SYSYTEMPARAMS,apiSystemparam.getName());
        //发送mq到网关
        mqUtil.sendMessage(SystemParams.SYSYTEMPARAMS, APIRoutingType.ADD);

    }

    @Override
    public PageInfo<ApiSystemparam> getSystemparamList(ApiSystemparam criteria, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return new PageInfo<>(systemparamDao.queryApiSystemparam(criteria));
    }

    @Override
    public void updateApiSystemparam(ApiSystemparam systemparam) {
        ApiSystemparam source = systemparamDao.getMappingById(systemparam.getId());
        if (source != null) {
            try {
                cacheService.sRemove(SystemParams.SYSYTEMPARAMS, source.getName());//不管如何更新先删除之前的
                //发送mq到网关
                mqUtil.sendMessage(SystemParams.SYSYTEMPARAMS, APIRoutingType.DELETE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        systemparamDao.updateApiSystemparam(systemparam);//更新数据
        if (systemparam.getState() == 1) {//如果是有效的数据,则再添加到 redis 中
            try {
                cacheService.sAdd(SystemParams.SYSYTEMPARAMS,systemparam.getName());
                //发送mq到网关
                mqUtil.sendMessage(SystemParams.SYSYTEMPARAMS, APIRoutingType.ADD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteSystemparam(int[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (int id : ids) {
            ApiSystemparam systemparam = systemparamDao.getMappingById(id);
            if (systemparam != null) {
                systemparam.setState(0);
                systemparamDao.updateApiSystemparam(systemparam);
                try {
                    cacheService.sRemove(SystemParams.SYSYTEMPARAMS, systemparam.getName());//从 redis 中删除
                    //发送mq到网关
                    mqUtil.sendMessage(SystemParams.SYSYTEMPARAMS, APIRoutingType.DELETE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ApiSystemparam getSystemparamById(int id) {
        return systemparamDao.getMappingById(id);
    }


}
