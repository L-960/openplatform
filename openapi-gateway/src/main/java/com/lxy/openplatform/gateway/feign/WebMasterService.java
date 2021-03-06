package com.lxy.openplatform.gateway.feign;


import com.lxy.openplatform.commons.webmaster.bean.AjaxMessage;
import com.lxy.openplatform.commons.webmaster.pojo.AppInfo;
import com.lxy.openplatform.commons.webmaster.pojo.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author Lvxy
 * @Date 2021/3/6 20:04
 * @Version 1.0
 * @Desc 调用webmaster接口
 */
@FeignClient(value = "OPENAPI-WEB-MASTER",fallback = WebMasterServiceFallback.class)
public interface WebMasterService {

    @RequestMapping(value = "/sys/app_info/update2",method = RequestMethod.POST)
    AjaxMessage update(@RequestBody AppInfo info) throws Exception;

    @RequestMapping(value = "/sys/customer/update2", method = RequestMethod.POST)
    AjaxMessage update(@RequestBody Customer Customer) throws Exception;

}
