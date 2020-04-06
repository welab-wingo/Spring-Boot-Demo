package com.wingo.shiro01.controller;

/**
 * @Author Wingo
 * @Date 2020-04-02 14:56
 * @Description
 */

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/index")
    public String hello(Model model){
        Subject subject = SecurityUtils.getSubject();
        model.addAttribute("username", subject.getSession().getAttribute("username"));
        return "index";
    }

    @GetMapping("/toAdd")
    public String add(){
        return "/user/add";
    }

    @GetMapping("/toUpdate")
    public String update(){
        return "/user/update";
    }

    @GetMapping("/toExit")
    public String exit(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String login(String username, String password, Model model){

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);

        try {
            Session session = subject.getSession();
            session.setAttribute("username", username);
            subject.login(token);
            return "redirect:/index";
        } catch (UnknownAccountException e) {

            model.addAttribute("msg", "用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e) {

            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }
}
