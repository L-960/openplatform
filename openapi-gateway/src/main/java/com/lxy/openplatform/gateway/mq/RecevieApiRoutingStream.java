package com.lxy.openplatform.gateway.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author Lvxy
 * @Date 2021/3/6 23:43
 * @Version 1.0
 * @Desc 只做接收
 */
public interface RecevieApiRoutingStream {

    @Input("WebMaster->GateWayChange")
    SubscribableChannel subscribable_channel();

}