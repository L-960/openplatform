package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.gateway.feign.CacheService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class JwtFilter extends ZuulFilter {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }


    /**
     * 理论上它是第一个需要校验的filter
     */
    @Override
    public int filterOrder() {
        return 9;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    /***
     用户可能会多次登陆,或者是在多个设备上登陆,如果我们的平台不允许用户多设备登陆,代表一旦在某个设备上登陆后,之前的token就要失效
     在此处我们应该要判断当前的用户的最新token和用户传递的token是不是一样.所以我一定要在某个地方保存有用户最新的token
     我们选择保存在数据库加redis,在这里应该是从redis进行查询看看用户的最新的token是什么
     校验JWT.如何失败会抛出异常
     */
    @Override
    public Object run() throws ZuulException {

        //拿到用户传递的jwt token
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String jwt = request.getHeader("token");

        //进行解析校验，看解析出来的信息是否合法
        // TODO 此处数据暂时写死 但是要与author-center模块一致 should do 去用户表查询
        try {
            // 使用密钥值去解析
            Claims body = Jwts.parser().setSigningKey("lxy123".getBytes()).parseClaimsJws(jwt).getBody();

            // 校验name
            String name = body.get("name").toString();

            // 校验id
            Object id = body.get("id");

            // 校验通过
            if ("1".equals(id) && "吕星宇".equals(name)){

                // 去redis查询正确的token
                String jwtfromRedis = cacheService.getFromRedis(SystemParams.JWT_TOKEN_REDIS_PRE + name);

                //如果没有异常代表校验成功,直接放行
                if (jwt != null && !jwt.equals(jwtfromRedis)) {
                    //缓存中放的最新的和用户传递的不一致的情况下,就直接扔出异常,给用户返回认证失败
                    throw new RuntimeException();
                }
                System.err.println(name);
                System.err.println(id);
                return null;

            }

        } catch (Exception e) {
            //不能访问
            context.setSendZuulResponse(false);
            HttpServletResponse response = context.getResponse();
            response.setContentType("application/json;charset=utf-8");
            BaseResultBean bean = new BaseResultBean();
            bean.setCode(ExceptionDict.UNLOGIN);
            bean.setMsg("尚未登陆认证");
            try {
                context.setResponseBody(objectMapper.writeValueAsString(bean));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
