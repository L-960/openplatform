package com.lxy.openapi.order.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Lvxy
 * @Date 2021/3/10 21:57
 * @Version 1.0
 * @Desc "{\"order_id\":\"1\",\"order_state\":\"1\"}"
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetBean {
    String order_id;
    String order_state;
}
