package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.commons.webmaster.pojo.AppInfo;
import com.lxy.openplatform.gateway.feign.CacheService;
import com.lxy.openplatform.gateway.cache.InitApiRouting;
import com.lxy.openplatform.gateway.cache.InitAppKey;
import com.lxy.openplatform.gateway.feign.WebMasterService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * @Author Lvxy
 * @Date 2021/3/6 17:53
 * @Version 1.0
 * @Desc 当前过滤器的主要作用是对免费的接口进行限流, 判断用户是否还有免费次数可以访问
 */

// todo 后面要写一个定时任务 0点恢复免费次数 别忘了

@Component
public class LimitFilter extends ZuulFilter {


    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Map<String, Object>> cache = InitApiRouting.API_ROUTING_MAP_CACHE;

    private Map<String, Map<String, Object>> appKeyMapCache = InitAppKey.APP_KEY_MAP_CACHE;

    @Autowired
    private WebMasterService appInfoService;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 当前过滤器的级别比路由的低一些,原因是我们必须要保证用户访问的是一个正确的服务才进行次数扣除,所以通过路由的过滤器来进行了这个合法性的判断
     * 注意并不是说路由的过滤器执行了就代表服务请求了,而是要等所有的前置过滤器都执行完成后才会请求服务,路由过滤器只是用来设置要请求什么服务
     */
    @Override
    public int filterOrder() {
        return 110;
    }

    @Override
    public boolean shouldFilter() {

        //当前的过滤器针对的是免费的服务,所以还是要先判断接口的收费性
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String method = request.getParameter("method");//用户要请求的服务的名字

        boolean isFree = true;

        try {
            //String free = cacheService.hGet(SystemParams.METHOD_REDIS_PRE + method, "needfee");
            // 替换缓存
            String free = cache.get(SystemParams.METHOD_REDIS_PRE + method).get("needfee").toString();
            isFree = "0".equals(free);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.sendZuulResponse() && isFree;
    }


    /**
     * 我们的目标是判断当前用户是否还有剩余的次数访问这个服务
     * 我们只需要知道剩余次数是多少就可以了
     * 我们只需要从redis中自减1得到的结果只要大于等于0就可以了
     * 获取用户传递的app_key
     */
    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String app_key = request.getParameter("appKey");

        Long times = null;//自减后的剩余次数

        try {
            AppInfo appInfo = new AppInfo();

            times = cacheService.hIncrementId(SystemParams.APPKEY_REDIS_PRE + app_key, "limit", -1L);

            // 与此同时 当前内存缓存中的免费次数也要修改
            appKeyMapCache.get(SystemParams.APPKEY_REDIS_PRE + app_key).put("limit",times);

            Map<String, Object> map = cacheService.hGetAll(SystemParams.APPKEY_REDIS_PRE + app_key);
            // 不能替换缓存

            if (times >= 0) {
                //TODO 将剩余的次数同步到数据库中
                BeanUtils.populate(appInfo,map);
                appInfoService.update(appInfo);
            }
            if (times < 0) {
                //剩余次数不够 不能访问了
                context.setSendZuulResponse(false);
                HttpServletResponse response = context.getResponse();
                response.setContentType("application/json;charset=utf-8");
                BaseResultBean bean = new BaseResultBean();
                bean.setCode(ExceptionDict.LIMIT_ERROR);
                bean.setMsg("超出本日的免费次数限制");
                try {
                    context.setResponseBody(objectMapper.writeValueAsString(bean));
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
