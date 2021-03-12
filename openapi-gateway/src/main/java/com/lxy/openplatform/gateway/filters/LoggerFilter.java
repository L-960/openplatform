package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.gateway.mq.SendLogStream;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.lxy.openplatform.gateway.bean.LoggerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * post过滤器 在返回接口数据的时候计算响应时间 将日志信息通过mq传输给logindex模块
 */
@Component
public class LoggerFilter extends ZuulFilter {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private SendLogStream sendLogStream;

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 50;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        LoggerBean loggerBean = new LoggerBean();

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        Date responeDate = new Date();

        //设置响应时间
        loggerBean.setResponseTime(responeDate);

        loggerBean.setApp_key(request.getParameter("appKey"));

        loggerBean.setApiName(request.getParameter("method"));

        loggerBean.setRemoteIp(request.getRemoteAddr());

        // 直接在上下文中拿刚才设置的起始时间
        Date startTine = (Date) context.get("startTime");

        loggerBean.setReveivceTime(startTine);

        loggerBean.setTotalTime(responeDate.getTime() - startTine.getTime());//设置总时间 毫秒

        try {
            loggerBean.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        loggerBean.setContent(request.getQueryString());

        MessageChannel messageChannel = sendLogStream.message_channel();

        try {
            String json = objectMapper.writeValueAsString(loggerBean);
            System.err.println("异步发送给log的数据："+json);
            // mq异步发送
            messageChannel.send(new GenericMessage<String>(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
