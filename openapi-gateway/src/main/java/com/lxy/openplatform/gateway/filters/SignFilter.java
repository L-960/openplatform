package com.lxy.openplatform.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.ExceptionDict;
import com.lxy.openplatform.commons.constans.SystemParams;
import com.lxy.openplatform.gateway.feign.CacheService;
import com.lxy.openplatform.gateway.cache.InitAppKey;
import com.lxy.openplatform.gateway.utils.Md5Util;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lvxy
 * @Date 2021/3/5 18:17
 * @Version 1.0
 * @Desc 当前的过滤器主要作用是用于校验用户的请求中的签名是否正确, 来判断是否是合法的请求
 */

@Component
@Slf4j
public class SignFilter extends ZuulFilter {

    @Autowired
    private CacheService cacheService;

    private Map<String, Map<String, Object>> appKeyMapCache = InitAppKey.APP_KEY_MAP_CACHE;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    /**
     * 规则,将用户传递的参数中除了签名之外的数据 按照key的字典顺序进行排序,
     * 然后将排序后数据按照key+value+key+value。。。的方式进行拼接字符串
     * 然后再获取用户的签名的秘钥(appsecret:根据appkey去redis中去取),进行拼接,得到一个新的字符串,然后计算md5值,将计算的结果和用户传递的数据进行比较,一致则放行
     * 需要的数据就是用户传递的参数, 用户的appkey,用户传递的签名
     */
    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        //获取所有的参数的名字
        Enumeration<String> parameterNames = request.getParameterNames();

        //用于保存除了签名之前的数据
        Map<String, String> signMap = new HashMap<>();

        while (parameterNames.hasMoreElements()) {
            //获取每一个参数的名字
            String name = parameterNames.nextElement();
            if (!"sign".equalsIgnoreCase(name)) {
                signMap.put(name, request.getParameter(name));//保存除了sign之外的数据
            }
        }

        //获取应用的app key
        String appKey = request.getParameter("appKey");
        try {
            //获取appsecret
            // String appSecret = cacheService.hGet(SystemParams.APPKEY_REDIS_PRE + appKey, "appSecret");
            // 替换缓存
            String appSecret = appKeyMapCache.get(SystemParams.APPKEY_REDIS_PRE + appKey).get("appSecret").toString();

            if (appSecret == null || StringUtils.isEmpty(appSecret)) {
                context.setSendZuulResponse(false);
                HttpServletResponse response = context.getResponse();
                response.setContentType("application/json;charset=utf-8");
                BaseResultBean bean = new BaseResultBean();
                bean.setCode(ExceptionDict.SYSTEMPARAM_SIGN_ERROR);
                bean.setMsg("请检查appKey");
                try {
                    context.setResponseBody(objectMapper.writeValueAsString(bean));
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }
            }
            // md5计算生成sign
            String md5Signature = Md5Util.md5Signature(signMap, appSecret);//计算用户传递的参数应该得到的签名

            // 获取用户传递的sign
            String userSign = request.getParameter("sign");//用户传递的签名

            // 日志记录
            //System.err.println("系统计算的签名:" + md5Signature);
            //System.err.println("用户传递的签名:" + userSign);
            log.warn("系统计算的签名:" + md5Signature);
            log.warn("用户传递的签名:" + userSign);

            if (md5Signature.equalsIgnoreCase(userSign)) {
                return null;//校验通过,后面就不走了
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        //不管发生什么情况导致的校验失败,最终都会来这里执行代码
        context.setSendZuulResponse(false);
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/json;charset=utf-8");
        BaseResultBean bean = new BaseResultBean();
        bean.setCode(ExceptionDict.SYSTEMPARAM_SIGN_ERROR);
        bean.setMsg("签名校验失败");
        try {
            context.setResponseBody(objectMapper.writeValueAsString(bean));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
