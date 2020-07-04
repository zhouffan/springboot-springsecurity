package com.example.springbootspringsecurity.controller;


import com.example.springbootspringsecurity.common.api.CommonResult;
import com.example.springbootspringsecurity.dto.UmsAdminLoginParam;
import com.example.springbootspringsecurity.entity.UmsAdmin;
import com.example.springbootspringsecurity.service.IUmsAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 后台用户表 前端控制器
 * </p>
 *
 * @author zhouxfu
 * @since 2020-07-04
 */
@RestController
@RequestMapping("/ums_admin")
public class UmsAdminController {
    @Autowired
    IUmsAdminService adminService;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @GetMapping("/permission1")
    @PreAuthorize("hasPermission('user', 'select')")
    public CommonResult permission1(){
        return CommonResult.success("permission1...");
    }
    @GetMapping("/home")
//    @PreAuthorize("hasPermission('user', 'select')")
    public CommonResult home(){
        return CommonResult.success("fetch...");
    }

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsAdmin> register(@RequestBody UmsAdmin umsAdminParam, BindingResult result) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }
}
