package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.gateway.feign.CacheService;
import com.lxy.openplatform.gateway.cache.InitApiRouting;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author Lvxy
 * @Date 2021/3/5 18:46
 * @Version 1.0
 * @Desc 幂等性：1分钟内只允许请求一次，常用于增删改
 * 这个是针对所有需要幂等性校验的服务进行的过滤器
 */
@Component
public class IdempotentsFilter extends ZuulFilter {

    @Autowired
    private CacheService cacheService;

    private Map<String, Map<String, Object>> cache = InitApiRouting.API_ROUTING_MAP_CACHE;

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
        return 40;
    }

    /**
     * 如果前面的过滤器拦截了请求,则不执行; 1 : 幂等
     * 同时如果当前请求的服务是一个非幂等性的服务,也不需要进行拦截,所以此处需要两个判断条件
     */
    @Override
    public boolean shouldFilter() {

        //根据用户请求的服务,获取到服务的信息,然后看看服务是不是需要幂等性的要求
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        //用户要请求的服务的名字
        String method = request.getParameter("method");

        Map<String, Object> map = null;

        // 根据method去缓存中查
        try {
            //map = cacheService.hGetAll(SystemParams.METHOD_REDIS_PRE + method);
            // 替换缓存
            map = cache.get(SystemParams.METHOD_REDIS_PRE + method);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 需要判断一下method，防止服务器报错崩溃
        if (map == null) {
            return false;
        }

        //幂等性默认是true
        boolean isIdempotents = true;

        //通过查询redis来获取这个服务的幂等性

        String idempotents = (String) map.get("idempotents");

        //判断是不是幂等性的
        isIdempotents = "1".equals(idempotents);

        //启用的条件中包含了是不是幂等性要求的服务
        return context.sendZuulResponse() && isIdempotents;
    }


    /**
     当前过滤器是要判断请求是不是已经执行过一次了
     根据我们的要求我们需要什么数据来进行这个判断
     要想判断请求是不是执行过,需要请求有一个唯一的可以区分的标识,签名,
     *签名是唯一的,因为不同的参数会有不同的签名,不同的app会有不同的appkey,相同的app不同的服务会有不同的method,相同的服务会有不同的参数,相同的参数中会有不同的时间戳
     *如果一切都一样,说明就是一个一样的请求
     请求被执行过一次之后可以将标识保存起来,当下次请求再来的时候拿到签名去redis看看这个标识存在还是不存在,不存在则代表没有执行过,存在则代表执行过
     所以可以用sign作为key向redis中保存任意一个数据,我们首先获取,没有就代表没有,有就代表执行过
    * */
    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        //获取用户传递的签名
        String sign = request.getParameter("sign");

        try {

            // 尝试获取当前签名对应的数据
            String idempotents = cacheService.getFromRedis(SystemParams.IDEMPOTENTS_REDIS_PRE + sign);

            // 如果不为空则代表是已经请求过了,如果为空则代表没有请求过
            // 已经请求过的逻辑，在此拦截
            if (idempotents != null) {
                context.setSendZuulResponse(false);
                HttpServletResponse response = context.getResponse();
                response.setContentType("application/json;charset=utf-8");
                BaseResultBean bean = new BaseResultBean();
                bean.setCode(ExceptionDict.SYSTEMPARAM_IDEMPOTENTS_ERROR);
                bean.setMsg("当前服务不允许重复请求");
                try {
                    context.setResponseBody(objectMapper.writeValueAsString(bean));
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            // 第一次去请求，放行，存入redis
            } else {
                //有效期不是永久的,因为一旦时间戳过期了,用户一定要传递新的时间,那么签名一定会发生变化,所以我们的有效期和时间戳的有效期保持一致即可
                cacheService.save2Redis(SystemParams.IDEMPOTENTS_REDIS_PRE + sign, System.currentTimeMillis()+"", TimeMillisValid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}