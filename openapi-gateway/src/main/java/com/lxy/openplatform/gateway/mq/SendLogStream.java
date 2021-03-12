package com.lxy.openplatform.gateway.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

// 发送
public interface SendLogStream {

    @Output("GateWay->LogIndexChange")
    MessageChannel message_channel();
}
