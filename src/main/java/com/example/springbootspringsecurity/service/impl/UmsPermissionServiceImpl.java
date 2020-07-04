package com.example.springbootspringsecurity.service.impl;

import com.example.springbootspringsecurity.entity.UmsPermission;
import com.example.springbootspringsecurity.mapper.UmsPermissionMapper;
import com.example.springbootspringsecurity.service.IUmsPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户权限表 服务实现类
 * </p>
 *
 * @author zhouxfu
 * @since 2020-07-04
 */
@Service
public class UmsPermissionServiceImpl extends ServiceImpl<UmsPermissionMapper, UmsPermission> implements IUmsPermissionService {

}
