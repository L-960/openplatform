package com.lxy.openplatform.commons.webmaster.pojo;


import lombok.Data;

import java.util.Date;

@Data
public class UserToken {
    private Integer id;
    private Integer cusId;
    private String accessToken;
    private Date expireTime;
    private Date startTime;
}
