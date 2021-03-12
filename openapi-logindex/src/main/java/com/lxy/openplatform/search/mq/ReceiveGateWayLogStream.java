package com.lxy.openplatform.search.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ReceiveGateWayLogStream {
    @Input("GateWay->LogIndexChange")
    SubscribableChannel SUBSCRIBABLE_CHANNEL();
}
