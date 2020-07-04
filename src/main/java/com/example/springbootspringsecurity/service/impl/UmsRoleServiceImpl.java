package com.example.springbootspringsecurity.service.impl;

import com.example.springbootspringsecurity.entity.UmsRole;
import com.example.springbootspringsecurity.mapper.UmsRoleMapper;
import com.example.springbootspringsecurity.service.IUmsRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户角色表 服务实现类
 * </p>
 *
 * @author zhouxfu
 * @since 2020-07-04
 */
@Service
public class UmsRoleServiceImpl extends ServiceImpl<UmsRoleMapper, UmsRole> implements IUmsRoleService {

}
