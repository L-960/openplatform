package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.gateway.feign.CacheService;
import com.lxy.openplatform.gateway.cache.InitSystemParam;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @Author Lvxy
 * @Date 2021/3/4 14:21
 * @Version 1.0
 * @Desc 过滤系统参数，无参数，则拦截
 */
@Component
public class SystemParamFilter extends ZuulFilter {

    @Autowired
    private CacheService cacheService;

    private Set<Object> systemParamCache = InitSystemParam.SYSTEM_PARAM_SET_CACHE;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        // 前置过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        Set<Object> sets = null;
        // 先去缓存中查询所有的系统参数,存储的是set集合
        try {
            //sets = cacheService.sMembers(SYSYTEMPARAMS);
            // 替换缓存
            sets = systemParamCache;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set存在性验证
        if (null == sets) {
            return null;
        }


        // 遍历sets
        for (Object set : sets) {

            String parameter = request.getParameter(set.toString());

            // 如果有参数
            if (parameter != null) {
                // 有参数 但是值为空
                if (StringUtils.isEmpty(parameter.trim())) {
                    context.setSendZuulResponse(false);
                    // 创建response
                    HttpServletResponse response = context.getResponse();
                    response.setContentType("application/json;charset=utf-8");

                    BaseResultBean bean = new BaseResultBean();
                    bean.setCode(ExceptionDict.SYSTEMPARAM_MISSED);
                    bean.setMsg("参数:" + set.toString() + "的值不合法");
                    try {
                        context.setResponseBody(objectMapper.writeValueAsString(bean));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }
                // 没参数
            } else {
                // 说明缺少系统参数
                context.setSendZuulResponse(false);
                // 创建response
                HttpServletResponse response = context.getResponse();
                response.setContentType("application/json;charset=utf-8");

                BaseResultBean bean = new BaseResultBean();
                bean.setCode(ExceptionDict.SYSTEMPARAM_MISSED);
                bean.setMsg("缺少系统参数：" + set.toString());
                try {
                    context.setResponseBody(objectMapper.writeValueAsString(bean));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }
}
