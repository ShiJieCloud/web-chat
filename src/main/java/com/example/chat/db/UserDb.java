package com.example.chat.db;

import com.example.chat.pojo.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据
 */
public class UserDb {
    //保存登录用户的昵称
    public static Map<String,User> userMap = new HashMap<>();
}
