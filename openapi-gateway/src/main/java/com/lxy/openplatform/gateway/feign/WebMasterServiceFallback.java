package com.lxy.openplatform.gateway.feign;


import com.lxy.openplatform.commons.webmaster.bean.AjaxMessage;
import com.lxy.openplatform.commons.webmaster.pojo.AppInfo;
import com.lxy.openplatform.commons.webmaster.pojo.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author Lvxy
 * @Date 2021/3/6 20:04
 * @Version 1.0
 * @Desc 调用webmaster接口
 */
@Component
public class WebMasterServiceFallback implements WebMasterService{

    public AjaxMessage update(AppInfo info) throws Exception{
        return null;
    }

    @Override
    public AjaxMessage update(Customer Customer) throws Exception {
        return null;
    }

}
