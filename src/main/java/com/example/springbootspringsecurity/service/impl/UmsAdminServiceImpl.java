package com.example.springbootspringsecurity.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootspringsecurity.common.util.JwtTokenUtil;
import com.example.springbootspringsecurity.entity.UmsAdmin;
import com.example.springbootspringsecurity.entity.UmsPermission;
import com.example.springbootspringsecurity.mapper.UmsAdminMapper;
import com.example.springbootspringsecurity.mapper.UmsAdminPermissionRelationMapper;
import com.example.springbootspringsecurity.service.IUmsAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author zhouxfu
 * @since 2020-07-04
 */
@Slf4j
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements IUmsAdminService {
    @Autowired
    private PasswordEncoder passwordEncoder; //使用加密

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UmsAdminPermissionRelationMapper umsAdminPermissionRelationMapper;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return getOne(wrapper);
    }

    @Override
    public UmsAdmin register(UmsAdmin umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(LocalDateTime.now());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdmin adminByUsername = getAdminByUsername(umsAdminParam.getUsername());
        if(adminByUsername != null){
            return null;
        }
        log.info(" register ===> " + umsAdmin);
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        log.info(" register ===> encodePassword:" + encodePassword);
        umsAdmin.setPassword(encodePassword);
        save(umsAdmin);
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info(" login===> password:"+password);
            log.info(" login===> userDetails:"+userDetails);
            //匹配验证密码
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            //
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            log.info(" login===> authentication:"+authentication);
            //
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //
            token = jwtTokenUtil.generateToken(userDetails);
            log.info(" login===> token:"+token);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {
        return umsAdminPermissionRelationMapper.getPermissionList(adminId);
    }
}
