package com.example.springbootspringsecurity.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhoudafu
 * @Date: 2020/7/2 20:56
 * @Description:
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/login")
    public String login(){
        return "static/login.html";
    }
    @PostMapping("/home")
    public String home(){
        return "xxxxxhomexxxxxxx";
    }
    @GetMapping("/test1")
    public String test1(){
        return "xxxxxxxxxxxx";
    }

    @GetMapping("/admin")
    public String test2(){
        return "xxxxxxadminxxxxxx";
    }
    @GetMapping("/user")
    public String test3(){
        return "xxxxxxxuserxxxxx";
    }

    @GetMapping("/logouthome")
    public String logouthome(){
        return "xxxxxxxlogouthomexxxxx";
    }
    @GetMapping("/logout")
    public String logout(){
        return "logout";
    }
    @PostMapping("/fail")
    public String fail(){
        return "xxxxxxxfailxxxxx";
    }
}
