package com.wingo.shiro01.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author Wingo
 * @Date 2020-04-02 19:54
 * @Description
 */
@Data
@Entity
@Table(name = "user")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        private String username;

        private String password;

        /** 权限信息 */
        private String perms;
}
