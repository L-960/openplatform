package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.commons.webmaster.pojo.Customer;
import com.lxy.openplatform.gateway.cache.InitAppKey;
import com.lxy.openplatform.gateway.feign.CacheService;
import com.lxy.openplatform.gateway.cache.InitApiRouting;
import com.lxy.openplatform.gateway.feign.WebMasterService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author Lvxy
 * @Date 2021/3/6 18:05
 * @Version 1.0
 * @Desc 当前过滤器的主要作用是对收费接口的计费处理
 */

//TODO 计费可能会分为很多情况,我们这个主要针对的就是按次固定扣费, 用户可能针对某个服务或者是针对所有服务有包月或者套餐服务
//TODO 实际应该根据服务的状态或者是客户的现在的套餐状况,以及每个服务的具体的钱数来进行代码编写

@Component
public class FeeFilter extends ZuulFilter {

    @Autowired
    private CacheService cacheService;

    private Map<String, Map<String, Object>> cache = InitApiRouting.API_ROUTING_MAP_CACHE;

    private Map<String, Map<String, Object>> appKeyMapCache = InitAppKey.APP_KEY_MAP_CACHE;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebMasterService webMasterService;

    @Value("${needfee}")
    private Integer needfee;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 120;
    }

    //当前的过滤器针对的是免费的服务,所以还是要先判断接口的收费性
    @Override
    public boolean shouldFilter() {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String method = request.getParameter("method");//用户要请求的服务的名字

        // 默认收费
        boolean isFree = true;
        try {
            //String free = cacheService.hGet(SystemParams.METHOD_REDIS_PRE + method, "needfee");
            // 替换缓存
            Object o = cache.get(SystemParams.METHOD_REDIS_PRE + method).get("needfee");
            isFree = "1".equals(o.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.sendZuulResponse() && isFree;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        String app_key = request.getParameter("appKey");

        //根据appkey找到其所属的客户id
        try {

            //String cusId = cacheService.hGet(SystemParams.APPKEY_REDIS_PRE + app_key, "cusId");
            // 替换缓存
            String cusId = appKeyMapCache.get(SystemParams.APPKEY_REDIS_PRE + app_key).get("cusId").toString();

            if (cusId != null) {

                Customer customer = new Customer();

                Long money = cacheService.hIncrementId(SystemParams.CUSTOMER_REDIS_PRE + cusId, "money", -needfee);

                Map<String, Object> map = cacheService.hGetAll(SystemParams.CUSTOMER_REDIS_PRE + cusId);

                // map->entity

                // todo 数据库扣费
                BeanUtils.populate(customer,map);
                webMasterService.update(customer);

                if (money < 0) {
                    //代表用户没钱了,我们要把上面扣除的给加回去,以免用户本来剩余1或者0的时候被我们减成了负数,这不合理
                    cacheService.hIncrementId(SystemParams.CUSTOMER_REDIS_PRE + cusId, "money", needfee);
                    // todo 数据库回弹
                    map.put("money",customer.getMoney()+needfee);
                    BeanUtils.populate(customer,map);
                    webMasterService.update(customer);

                }else{
                    //有钱的情况下是不拦截的
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //根据客户id对客户的钱进行计费处理
        context.setSendZuulResponse(false);
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/json;charset=utf-8");
        BaseResultBean bean = new BaseResultBean();
        bean.setCode(ExceptionDict.FEE_ERROR);
        bean.setMsg("余额不足");
        try {
            context.setResponseBody(objectMapper.writeValueAsString(bean));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

