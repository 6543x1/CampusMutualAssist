package com.jessie.campusmutualassist.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint("/websocket/{code}")
@Component
public class WebSocketServer {
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<String, WebSocketServer>();

    /**
     * 与客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收识别码
     */
    private String code = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code) {
        System.out.println("open");
        this.session = session;
        //如果存在就先删除一个，防止重复推送消息，实际这里实现了set，不删除问题也不大
        webSocketMap.put(code, this);
        this.code = code;
        log.info("建立WebSocket连接，code：" + code + "，当前连接数：" + webSocketMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketMap.remove(code);
        log.info("关闭WebSocket连接，code：" + this.code + "，当前连接数：" + webSocketMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来[" + code + "]的信息:" + message);

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    public void sendAll(String message) {
        log.info("推送消息到" + code + "，推送内容:" + message);
        for (WebSocketServer item : webSocketMap.values()) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定点推送
     */
    public boolean sendTo(String message, @PathParam("code") String code) {
        try {
            WebSocketServer item = webSocketMap.get(code);
            if (item.code.equals(code)) {
                log.info("推送消息到[" + code + "]，推送内容:" + message);
                item.sendMessage(message);
            }
        } catch (NullPointerException e) {
            log.info("推送失败：当前用户不在线");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, code);
    }
}