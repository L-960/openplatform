package com.lxy.openplatform.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @Author Lvxy
 * @Date 2021/3/4 16:31
 * @Version 1.0
 * @Desc
 */
public class ServiceParamsFiltr extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 98;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取用户的method的参数, 根据它去redis查询当前这个服务需要的参数,然后剩下的和公共参数的一样
        return null;
    }
}
