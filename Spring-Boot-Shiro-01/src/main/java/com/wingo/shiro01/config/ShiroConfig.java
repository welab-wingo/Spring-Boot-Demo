package com.wingo.shiro01.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.wingo.shiro01.shiro.UserRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author Wingo
 * @Date 2020-04-02 14:35
 * @Description
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Autowired DefaultWebSecurityManager securityManager){

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);


        // 设置过滤器链
        Map<String,String> filterMap = new LinkedHashMap<>(16);

        filterMap.put("/index", "anon");
        filterMap.put("/favicon.ico", "anon");

        // 添加访问权限
        filterMap.put("/toAdd", "perms[user:add]");
        filterMap.put("/toUpdate", "perms[user:update]");

        // 注意：要放在允许访问的页面后面，否则允许访问无效
        filterMap.put("/**", "authc");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 未授权跳转页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Autowired UserRealm userRealm){

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        //关联 realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    @Bean
    public UserRealm getRealm(){
        return new UserRealm();
    }

    /** 配置 ShiroDialect，用于 Thymeleaf 和 Shiro 标签配合使用 */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}
