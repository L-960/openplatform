package com.lxy.openapi.order.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author Lvxy
 * @Date 2021/3/10 21:57
 * @Version 1.0
 * @Desc
 * {"order_id":"1","cancel_type":"1","cancel_reason":"不想买","exp":"0"}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderCancelBean {

    String order_id;

    String cancel_type;

    String cancel_reason;

    String exp;
}
