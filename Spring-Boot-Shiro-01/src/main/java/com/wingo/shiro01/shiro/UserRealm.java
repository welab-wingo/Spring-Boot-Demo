package com.wingo.shiro01.shiro;

import com.wingo.shiro01.entity.User;
import com.wingo.shiro01.service.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Wingo
 * @Date 2020-04-02 14:40
 * @Description
 */

@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        log.info("执行授权逻辑");
        Subject subject = SecurityUtils.getSubject();
        User user = userRepository.findUserByUsername((String) subject.getSession().getAttribute("username"));

        // 给用户进行授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermission(user.getPerms());

        return info;
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
        log.info("执行认证逻辑");

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();


        UsernamePasswordToken token = (UsernamePasswordToken)arg0;
        User user = userRepository.findUserByUsername(token.getUsername());
        session.setAttribute("username", token.getUsername());
        if(user==null){
            // shiro 底层会抛出UnKnowAccountException
            return null;
        }

        // 判断密码
        return new SimpleAuthenticationInfo(user,user.getPassword(),getName());
    }
}
