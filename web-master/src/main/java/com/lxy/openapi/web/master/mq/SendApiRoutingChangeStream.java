package com.lxy.openapi.web.master.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @Author Lvxy
 * @Date 2021/3/6 23:10
 * @Version 1.0
 * @Desc 更新时 发送mq 让网关同步更新二级缓存
 */
public interface SendApiRoutingChangeStream {

    @Output("WebMaster->GateWayChange")
    MessageChannel message_channel();

}