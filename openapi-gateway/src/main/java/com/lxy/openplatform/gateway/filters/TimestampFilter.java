package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Lvxy
 * @Date 2021/3/5 18:05
 * @Version 1.0
 * @Desc 当前过滤器主要是对用户请求的时间进行校验, 判断是否在我们要求的时间范围内
 */
@Component
public class TimestampFilter extends ZuulFilter {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${TimeMillisValid}")
    private Integer TimeMillisValid;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {

        //获取到用户传递的时间戳,我们要求的是到秒级别,同时我们要求传递的时间戳格式为yyyy-MM-dd HH:mm:ss
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String timestamp = request.getParameter("timestamp");

        //获取当前的系统时间
        //比较两个时间是不是在允许的范围差内
        try {

            //解析时间戳
            Date requestDate = simpleDateFormat.parse(timestamp);

            //获取当前时间的毫秒值
            long currentTimeMillis = System.currentTimeMillis();

            //用户传递的毫秒值
            long requestDateTime = requestDate.getTime();

            // 比较
            //如果当前服务器的时间小于用户的传递时间或者是当前服务器的时间和用户的请求时间差大于1分钟,则代表请求过期
            if (currentTimeMillis - requestDateTime < 0 || currentTimeMillis - requestDateTime > TimeMillisValid) {
                context.setSendZuulResponse(false);
                HttpServletResponse response = context.getResponse();
                response.setContentType("application/json;charset=utf-8");
                BaseResultBean bean = new BaseResultBean();
                bean.setCode(ExceptionDict.SYSTEMPARAM_TIME_STAMP_ERROR);
                bean.setMsg("时间戳格式不对或已过期");
                try {
                    context.setResponseBody(objectMapper.writeValueAsString(bean));
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.setSendZuulResponse(false);
            HttpServletResponse response = context.getResponse();
            response.setContentType("application/json;charset=utf-8");
            BaseResultBean bean = new BaseResultBean();
            bean.setCode(ExceptionDict.SYSTEMPARAM_TIME_STAMP_ERROR);
            bean.setMsg("时间戳格式不对或已过期");
            try {
                context.setResponseBody(objectMapper.writeValueAsString(bean));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
