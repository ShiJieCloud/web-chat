package com.example.chat.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName: Message
 * @Description: 服务器发送给浏览器的 WebSocket 数据
 * @Author: Jie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResponseMessage {

    private boolean isSystem;
    private boolean status;
    private String fromUserId;
    private Object message;

}
