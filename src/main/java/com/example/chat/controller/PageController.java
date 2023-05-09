package com.example.chat.controller;

import com.example.chat.db.UserDb;
import com.example.chat.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Controller
public class PageController {

    @RequestMapping("/")
    public String toLogin(){
        return "login";
    }

    @PostMapping("/chat")
    public String doLogin(String username,HttpSession httpSession, Model model){
        //生成用户信息
        String userId = UUID.randomUUID().toString().replace("-", "");
        String avatarUrl = "/static/image/avatar/avatar-"+(System.currentTimeMillis()%43+1)+".png";
        User user = new User(userId, username, avatarUrl);
        //存储user到userMap中
        UserDb.userMap.put(userId,user);
        //将用户id保存到 HttpSession 中
        httpSession.setAttribute("loginUserId", userId);
        model.addAttribute("loginUser", user);
        return "chat";
    }

}
