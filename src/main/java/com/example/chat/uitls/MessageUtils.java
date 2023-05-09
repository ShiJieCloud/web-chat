package com.example.chat.uitls;

import com.example.chat.pojo.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName: Message
 * @Description: 浏览器发送给服务器的 WebSocket 数据
 * @Author: Jie
 */
public class MessageUtils {

    public static String getMessage(boolean isSystemMessage,boolean status,String fromUserId,Object message){
        try {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage
                    .setSystem(isSystemMessage)
                    .setStatus(status)
                    .setMessage(message);
            if(fromUserId!=null){
                responseMessage.setFromUserId(fromUserId);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(responseMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
