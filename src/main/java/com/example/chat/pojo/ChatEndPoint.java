package com.example.chat.pojo;

import com.example.chat.db.UserDb;
import com.example.chat.uitls.MessageUtils;
import com.example.chat.ws.GetHttpSessionConfigurator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/wechat",configurator = GetHttpSessionConfigurator.class)
public class ChatEndPoint {

    // 用Map集合存储所有客户端对应的ChatEndPoint对象
    private static Map<String, ChatEndPoint> onlineUsers = new ConcurrentHashMap<>();

    // 声明session对象，通过该对象可以发送消息给指定的客户端（用户）
    private Session session;

    // 声明一个httpSession对象，从中获取登录用户的邮箱
    private HttpSession httpSession;

    private Set<String> getIds(){
        return onlineUsers.keySet();
    }

    /**
     * 连接建立时被调用
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //将局部Session对象赋值给成员session
        this.session = session;
        //获取httpSession对象
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;

        String userId = (String) httpSession.getAttribute("loginUserId");
        System.out.println(userId+"登录聊天室...."+HttpSession.class.getName());

        //将当前 ChatEndPoint 对象存储到onlineUsers Map中
        onlineUsers.put(userId, this);

        //将当前在线用户的用户名推送给所有的客户端
        //1.生成系统消息
        String message = MessageUtils.getMessage(true, true,null, UserDb.userMap.values());
        //2.调用方法推送消息
        broadcastAllUsers(message);

    }

    //广播消息
    private void broadcastAllUsers(String message) {
        try {
            //将消息推送给所有的客户端
            for (String id : getIds()) {
                ChatEndPoint chatEndPoint = onlineUsers.get(id);
                //调用session对象send推送消息
                //chatEndPoint.session.getAsyncRemote().sendText(message);
                chatEndPoint.session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收到客户端发送的数据时被调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            //将消息发送给其他用户

            //将message的json格式转为message对象
            ObjectMapper mapper = new ObjectMapper();
            Message mess = mapper.readValue(message, Message.class);

            //获取接收数据
            String toUserId = mess.getToUserId();
            String data = mess.getMessage();

            //获取当前登录的用户
            String loginUserId = (String) httpSession.getAttribute("loginUserId");

            //生成推送给用户的消息格式
            String resultMessage = MessageUtils.getMessage(false, false, loginUserId, data);
            //使用指定用户的session对象发送消息给指定用户
            onlineUsers.get(toUserId).session.getBasicRemote().sendText(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 连接关闭时被调用
     */
    @OnClose
    public void onClose(Session session) {
        String loginUserId = (String) httpSession.getAttribute("loginUserId");
        System.out.println(loginUserId + "WS连接关闭了....");

        //移除用户Session
        onlineUsers.remove(loginUserId);
        //移除用户昵称
        UserDb.userMap.remove(loginUserId);

        //通知所有客户端用户下线
        Set<String> set = new HashSet<>();
        set.add(loginUserId);
        //1.生成系统消息
        String message = MessageUtils.getMessage(true,false, null, set);
        //2.调用方法推送消息
        broadcastAllUsers(message);

    }

}
