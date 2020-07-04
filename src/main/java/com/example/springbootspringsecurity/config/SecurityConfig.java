package com.example.springbootspringsecurity.config;

import com.example.springbootspringsecurity.component.JwtAuthenticationTokenFilter;
import com.example.springbootspringsecurity.component.RestAuthenticationEntryPoint;
import com.example.springbootspringsecurity.component.RestfulAccessDeniedHandler;
import com.example.springbootspringsecurity.dto.AdminUserDetails;
import com.example.springbootspringsecurity.entity.UmsAdmin;
import com.example.springbootspringsecurity.entity.UmsPermission;
import com.example.springbootspringsecurity.service.IUmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


/**
 * SpringSecurity的配置
 *
 */
//@Configuation等价于<Beans></Beans>
//@Bean等价于<Bean></Bean>
//@ComponentScan等价于<context:component-scan base-package=”com.dxz.demo”/>
//定义配置类，可替换xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法
@Configuration
//@Import 将该类实例注入到ioc容器中
// 1: 加载了WebSecurityConfiguration配置类, 配置安全认证策略。
// 2: 加载了AuthenticationConfiguration, 配置了认证信息。
@EnableWebSecurity
//开启基于方法的安全认证机制，也就是说在web层的controller启用注解机制的安全确认。@PreAuthorize("hasAuthority('admin')")才会生效
@EnableGlobalMethodSecurity(prePostEnabled=true)
//继承WebSecurityConfigurerAdapter
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private IUmsAdminService adminService;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //由于使用的是JWT，我们这里不需要csrf    https://www.jianshu.com/p/c69f08ca056d
        //CSRF 攻击简单来说，是多Tab页面浏览器的一个安全漏洞，比如你正在访问A网站，此时如果浏览器有你的cookie，并且session没有过期，此时你去访问B网站，那么B网站可以直接调用A网站的接口，而A网站则认为是你本人进行的操作。
        //csrf：跨站请求伪造，默认开启。需要在请求接口的时候加入csrfToken才行。
        httpSecurity.csrf()
                .disable()
                .sessionManagement()// 基于token，所以不需要session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, // 允许对于网站静态资源的无授权访问
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/swagger-resources/**",
                        "/v2/api-docs/**"
                )
                .permitAll()
                .antMatchers("/ums_admin/login", "/ums_admin/register")// 对登录注册要允许匿名访问
                .permitAll()
                .antMatchers(HttpMethod.OPTIONS)//跨域请求会先进行一次options请求
                .permitAll()
//                .antMatchers("/**")//测试时全部运行访问
//                .permitAll()
                .anyRequest()// 除上面外的所有请求全部需要鉴权认证
                .authenticated();
        // 禁用缓存
        httpSecurity.headers().cacheControl();
        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //添加自定义未授权和未登录结果返回
        httpSecurity.exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    //注入bean， 注册添加用户时，需要PasswordEncoder 密码加密后保存
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                UmsAdmin admin = adminService.getAdminByUsername(username);
//                if (admin != null) {
//                    List<UmsPermission> permissionList = adminService.getPermissionList(admin.getId());
//                    return new AdminUserDetails(admin,permissionList);
//                }
//                throw new UsernameNotFoundException("用户名或密码错误");
//            }
//        };

        //获取登录用户信息
        return username -> {
            UmsAdmin admin = adminService.getAdminByUsername(username);
            if (admin != null) {
                List<UmsPermission> permissionList = adminService.getPermissionList(admin.getId());
                return new AdminUserDetails(admin,permissionList);
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
