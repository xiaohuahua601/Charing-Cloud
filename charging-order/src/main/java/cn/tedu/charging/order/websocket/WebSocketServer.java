package cn.tedu.charging.order.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;

/**
 * 定义WebSocketServer endpoint 定义链接地址
 * userId webserver 可以指定 是哪个用户建立了链接
 * @Author: goodman
 * @Date: 2025/3/9 19:45
 */
@Slf4j
@Component
@ServerEndpoint("/ws/server/{userId}")
public class WebSocketServer {

    /**
     * 用户id和用户session存储到本地的map
     * 本地map的缺点
     * 重启数据丢失问题
     * 集群问题数据独享问题
     * 数据量过大问题
     * 可以自己去了解 分布式session websocket 集群问题
     */
    private static HashMap<Integer,Session> sessionMap = new HashMap<>();

    /**
     * 用户和服务端建立链接 会调用 open方法
     * @PathParam 用来取用户id
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") Integer userId){
        log.debug("用户:{},建立连接",userId);
        sessionMap.put(userId,session);
    }

    /**
     * 用户断开连接
     * @param session
     * @param userId
     */
    @OnClose
    public void onClose(Session session,@PathParam("userId") Integer userId){
        log.debug("用户:{},关闭连接",userId);
        sessionMap.put(userId,session);
    }

    /**
     * 发生错误
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session,Throwable throwable){
        log.error("发生错误",throwable);
    }

    /**
     * 服务端收到客户端 消息
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session){
        log.debug("服务端收到了客户端发送的消息:{}",message);
    }

    /**
     * 服务端发送消息给客户端
     * @param userId
     * @param message
     */
    public void sendMessage(Integer userId,String message){
        log.debug("服务端给用户:{},发送消息:{},",userId,message);
        //从本地map中找到用户id对应的session
        Session session = sessionMap.get(userId);
        if (session != null) {
            //通过session会话发送消息
            session.getAsyncRemote().sendText(message);
        }else{
            log.error("用户:{},没有建立链接",userId);
        }
    }
}
