package com.wingo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author Wingo
 * @Date 2020-04-06 18:05
 * @Description
 */
@Data
public class User {

    @TableId(type=IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
