package com.lxy.openapi.web.master.mq;

import com.lxy.openplatform.commons.APIRoutingType;
import com.lxy.openplatform.commons.beans.ApiRoutingMQBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * @Author Lvxy
 * @Date 2021/3/6 23:15
 * @Version 1.0
 * @Desc
 */
@Component
public class MqUtil {

    @Autowired
    private SendApiRoutingChangeStream sendApiRoutingChangeStream;

    public void sendMessage(String key, APIRoutingType type) {

        MessageChannel channel = sendApiRoutingChangeStream.message_channel();

        ApiRoutingMQBean bean = new ApiRoutingMQBean();

        bean.setKey(key);

        bean.setType(type);

        channel.send(new GenericMessage<ApiRoutingMQBean>(bean));

    }
}
