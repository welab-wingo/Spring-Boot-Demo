package com.wingo.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wingo.mybatisplus.entity.User;
import com.wingo.mybatisplus.mapper.UserMapper;
import com.wingo.mybatisplus.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author Wingo
 * @Date 2020-04-06 18:16
 * @Description
 */
@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/selectList")
    @ResponseBody
    public ResponseVO selectList(){
        ResponseVO responseVO = new ResponseVO();
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().orderByDesc("id"));
        responseVO.setData("userList",userList);
        return responseVO;
    }

    @GetMapping(value = "/insert/{name}/{age}")
    public String insert(@PathVariable("name") String name, @PathVariable("age") Integer age){
        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(name + age +"@wingo-email.com");
        userMapper.insert(user);
        return "redirect:/selectList";
    }

    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        userMapper.deleteById(id);
        return "redirect:/selectList";
    }

    @GetMapping(value = "/age-eq/{age}")
    public ResponseVO ageEqual(@PathVariable("age") Integer age){
        ResponseVO responseVO = new ResponseVO();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("age",age);
        List<User> userList = userMapper.selectList(queryWrapper);
        responseVO.setData("userList",userList);
        return responseVO;
    }

    @GetMapping(value = "/age/{gt}/{lt}")
    @ResponseBody
    public ResponseVO age(@PathVariable("gt") Integer gt, @PathVariable("lt") Integer lt){
        ResponseVO responseVO = new ResponseVO();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age",gt);
        queryWrapper.lt("age",lt);
        List<User> userList = userMapper.selectList(queryWrapper);
        responseVO.setData("userList",userList);
        return responseVO;
    }
}
