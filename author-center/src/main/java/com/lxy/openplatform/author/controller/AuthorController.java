package com.lxy.openplatform.author.controller;

import com.lxy.openplatform.author.feign.CacheService;
import com.lxy.openplatform.author.pojo.User;
import com.lxy.openplatform.commons.beans.BaseResultBean;
import com.lxy.openplatform.commons.constans.SystemParams;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lvxy
 * @Desc 提供登录鉴权 jwt 生成存放信息的token 在网关中解析token
 */
@RestController
public class AuthorController {

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/login")
    public BaseResultBean auther(@RequestBody User user, HttpServletResponse response) {

        BaseResultBean bean = new BaseResultBean();

        // TODO 去数据库用户表查询相关信息

        if ("admin".equals(user.getUsername()) && "admin".equals(user.getPassword())) {

            //帐号信息是对的,直接给用户返回JWT相关的信息
            bean.setCode("1");

            bean.setMsg("登陆成功");

            // 转成Date无需修正时间 +8小时 当前登录时间
            //Instant now = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
            Instant now = Instant.now();

            // 毫秒 1min * 60
            //System.out.println(Date.from(now.plusMillis(60000*60)));

            String jwt = Jwts.builder()
                    //.setSubject("admin") // TODO 数据库表 设置当前的用户是谁,这里的任何信息都可以随便写,需要后续拿出来处理
                    .setIssuedAt(Date.from(now)) // 设置开始的有效期为当前服务器时间
                    .setExpiration(Date.from(now.plusMillis(60000))) // 设置过期时间 1min
                     // 可以随便内容,主要是键值对,可以在需要的地方拿出来
                    .claim("id", 1)
                    .claim("name", "吕星宇") // TODO 数据库表
                    .signWith(SignatureAlgorithm.HS256,"lxy123".getBytes()) // TODO 设置签名的算法和秘钥值
                    .compact();

            System.err.println(jwt);

            // 将token放到响应头中返回
            response.addHeader("token",jwt);

            // 更新redis缓存
            try {
                // {key:TOKEN:admin ,value:token值} 所以我们的用户名字不能重复 或者在这里拼接uuid
                cacheService.save2Redis(SystemParams.JWT_TOKEN_REDIS_PRE + user.getUsername(), jwt,60*1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            bean.setCode("0");
            bean.setMsg("帐号密码错误");
        }

        return bean;
    }
}
