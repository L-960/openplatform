package com.lxy.openplatform.testservice01.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testservice01")
public class Test01Controller {

    @RequestMapping("/test01")
    public String test01(String name){

        return "Testservice01==>test01收到的name--->" + name;
    }

    @RequestMapping("/test02/{name}")
    public String test02(@PathVariable String name){

        return "Testservice01==>test02 rest 收到的name--->" + name;
    }

    @RequestMapping("/test03/{name}/{age}")
    public String test03(@PathVariable String name, @PathVariable int age){

        return "Testservice01==>test03 rest 收到的name--->" + name+"    age===>"+age;
    }

}
