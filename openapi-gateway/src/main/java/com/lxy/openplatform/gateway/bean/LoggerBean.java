package com.lxy.openplatform.gateway.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


// 通过mq发送消息的bean

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerBean {
    // appkey
    private String app_key;
    // 访问的接口名
    private String apiName;
    // 保存用户的访问ip
    private String remoteIp;
    // 服务ip
    private String serverIp;
    // 接受时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date reveivceTime;
    // 响应时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date responseTime;
    // 总耗时
    private long totalTime;
    // 参数内容
    private String content;

}
