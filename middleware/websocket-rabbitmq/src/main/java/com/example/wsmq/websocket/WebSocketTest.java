package com.example.wsmq.websocket;

import com.example.wsmq.rabbitMq.RabbitManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/websocket/arMap/{userId}")
public class WebSocketTest {

    public static final ConcurrentHashMap<String, Integer> onlineCount = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketTest>> userWebSocketMap = new ConcurrentHashMap<>();

    private static final String EXCHANGE_NAME = "Meta.LabelService.RealTime.LabelData";
    private static final String QUEUE_NAME_PREFIX = "Label.Data.Screen.User.";
    private static final String ROUTING_KEY_LABEL_DATA = "LabelData.RealTime.*.PTTYPE";

    private String userId;
    private Session session;

    private static RabbitManage rabbitManage;

    @Autowired
    public void setRabbitManage(RabbitManage rabbitManage) {
        WebSocketTest.rabbitManage = rabbitManage;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") final String userId) {
        this.session = session;
        this.userId = userId;
        if (!existUser(userId)) {
            initUserInfo(userId);
        } else {
            CopyOnWriteArraySet<WebSocketTest> webSocketTestSet = getUserSocketSet(userId);
            webSocketTestSet.add(this);
            userCountIncrease(userId);
        }
        log.debug("有新连接加入！{}当前连接在线人数：{}", userId, getCurrUserCount(userId));
        // 首次建立连接时，声明队列、并监听
        declareQueueWhenFirstEstablished();
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<WebSocketTest> webSocketTestSet = userWebSocketMap.get(userId);
        //从set中删除
        webSocketTestSet.remove(this);
        //在线数减1
        userCountDecrement(userId);
        log.debug("关闭一条连接！{}当前连接在线人数：{}", userId, getCurrUserCount(userId));
        // 当所有用户断开连接时，删除队列
        deleteQueueWhenAllUserDisconnect();
        //
    }

    @OnMessage
    public void onMessage(String message) {
        log.debug("来自客户端{}的消息：{}", userId, message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // log.error("发生错误");
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static Integer getCurrUserCount(String userId) {
        return onlineCount.get(userId);
    }

    public static CopyOnWriteArraySet<WebSocketTest> getUserSocketSet(String userId) {
        return userWebSocketMap.get(userId);
    }

    private void declareQueueWhenFirstEstablished() {
        if (getUserSocketSet(userId).size() != 1) {
            return;
        }
        rabbitManage.declareQueue(EXCHANGE_NAME, QUEUE_NAME_PREFIX + userId, ROUTING_KEY_LABEL_DATA);
    }

    private void deleteQueueWhenAllUserDisconnect() {
        CopyOnWriteArraySet<WebSocketTest> userSocketSet = getUserSocketSet(this.userId);
        if (userSocketSet.size() == 0) {
            rabbitManage.deleteQueue(QUEUE_NAME_PREFIX + userId);
            // todo 发送mq请求，关闭客户端，释放远端资源
        }
    }

    private boolean existUser(String userId) {
        return userWebSocketMap.containsKey(userId);
    }

    private void userCountIncrease(String userId) {
        if (onlineCount.containsKey(userId)) {
            onlineCount.put(userId, onlineCount.get(userId) + 1);
        }
    }

    private void userCountDecrement(String userId) {
        if (onlineCount.containsKey(userId)) {
            onlineCount.put(userId, onlineCount.get(userId) - 1);
        }
    }

    private void removeUserCount(String userId) {
        onlineCount.remove(userId);
    }

    private void initUserInfo(String userId) {
        CopyOnWriteArraySet<WebSocketTest> webSocketTestSet = new CopyOnWriteArraySet<>();
        webSocketTestSet.add(this);
        userWebSocketMap.put(userId, webSocketTestSet);
        onlineCount.put(userId, 1);
    }

}