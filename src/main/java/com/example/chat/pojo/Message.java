package com.example.chat.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Message
 * @Description: 浏览器发送给服务器的 WebSocket 数据
 * @Author: Jie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String toUserId;
    private String message;
}
