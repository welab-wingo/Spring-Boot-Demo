package com.wingo.shiro01.service;

import com.wingo.shiro01.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Wingo
 * @Date 2020-04-02 20:05
 * @Description
 */
public interface UserRepository extends JpaRepository<User, Integer> {


    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findUserByUsername(String username);
}
