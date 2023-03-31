package com.example.netty.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.netty.server.model.BraceletMessage;
import com.example.netty.server.model.BraceletMessageTypeConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>netty服务端处理类</h3>
 * {@link io.netty.channel.ChannelInboundHandlerAdapter}    服务端通讯基类
 * <p>
 * {@link io.netty.channel.ChannelOutboundHandlerAdapter}   客户端通讯基类
 * <p>
 * 这里继承springboot提供的{@link SimpleChannelInboundHandler}来快速实现我们的服务端通讯处理程序
 * <p>
 * 注意：由于在{@link io.netty.channel.ChannelInitializer#initChannel(Channel)}中使用了new NettyServerHandler()实例的方式，
 * 导致NettyServerHandler并未被spring容器托管，所以在此类中通过
 * {@link org.springframework.beans.factory.annotation.Autowired @Autowired}自动注入的任何Bean均为null
 * </p>
 * <p>
 * 解决办法：
 * <br>第一种：通过{@link org.springframework.context.ApplicationContext#getBean(String) ApplicationContext.getBean(String)}
 * 的方式获取到spring容器中的Bean对象
 * <br>第二种：
 * <pre>
 * {@code @Component}   // 将类交予spring管理
 * public class ServerHandler extends ChannelInboundHandlerAdapter {
 *     {@code @Autowired}
 *     private RestTemplate restTemplate;
 *
 *     private static ServerHandler handler;
 *
 *     // 如果想在生成对象时完成某些初始化操作，但是初始化操作又依赖于依赖注入，故无法在构造函数中实现
 *     // 为此，可以使用@PostConstruct注解方法来完成初始化
 *     // @PostConstruct注解的方法将会在依赖注入完成后被自动调用。
 *     // Bean初始化顺序：Constructor -> @Autowired -> @PostConstruct
 *     {@code @PostConstruct}
 *     public void init() {
 *         handler = this;
 *     }
 *
 *     {@code @Override}
 *     public void channelActive(ChannelHandlerContext ctx) throws Exception {
 *         System.out.println(handler.restTemplate);
 *         super.channelActive(ctx);
 *     }
 * }
 * </pre>
 * </p>
 */
@Slf4j
@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    // netty
    private static final AbstractEnvironment ENVIRONMENT = SpringUtil.getBean(AbstractEnvironment.class);

    /**
     * 有客户端连接服务器会触发此函数
     *
     * @param ctx 通道
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = socket.getAddress().getHostAddress();
        int clientPort = socket.getPort();
        // 获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();
        // 如果map中不包含此连接，就保存连接
        if (ChannelMap.getChannelMap().containsKey(channelId)) {
            log.info("客户端：{} 已存在建立的连接，当前连接通道数量：{} ", channelId, ChannelMap.getChannelMap().size());
        } else {
            //保存连接
            ChannelMap.addChannel(channelId, ctx.channel());
            log.info("客户端：{}，[IP：{} PORT：{}]连接netty服务器", channelId, clientIp, clientPort);
            log.info("当前连接通道数量: {}", ChannelMap.getChannelMap().size());
        }
        System.out.println(ENVIRONMENT.getProperty("remote"));
    }

    /**
     * 有客户端终止连接服务器会触发此函数
     *
     * @param ctx 通道处理程序上下文
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        // 存在客户端
        if (ChannelMap.getChannelMap().containsKey(channelId)) {
            // 删除连接
            ChannelMap.getChannelMap().remove(channelId);
            log.info("客户端：{}，[IP：{} PORT：{}]断开netty服务器", channelId, clientIp, inSocket.getPort());
            log.info("当前连接通道数量: " + ChannelMap.getChannelMap().size());
        }
    }

    /**
     * 有客户端发消息会触发此函数
     *
     * @param ctx 通道处理程序上下文
     * @param msg 客户端发送的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.debug("加载客户端报文,客户端id:{},客户端消息:{}", ctx.channel().id(), msg);
        // 根据规则对接收到的报文组数据切分
        String[] messageGroupArray = StrUtil.subBetweenAll(msg, StrUtil.BRACKET_START, StrUtil.BRACKET_END);
        List<String> dataSplitList;
        BraceletMessage braceletMessage;
        List<BraceletMessage> messageList = new ArrayList<>();
        for (String message : messageGroupArray) {
            try {
                // 按分隔符'*'对报文数据切分
                dataSplitList = StrUtil.split(message, '*');
                // 截取报文数据类型和和数据内容
                String content = dataSplitList.get(3);
                // 截取内容中第一个逗号之前的类型
                String dataType = StrUtil.subBefore(content, StrUtil.COMMA, false);
                // 截取内容中第一个逗号之后的内容
                String dataContent = StrUtil.subAfter(content, StrUtil.COMMA, false);
                // 装配模型
                braceletMessage = new BraceletMessage();
                braceletMessage.setOriginalMsg(StrUtil.BRACKET_START + message + StrUtil.BRACKET_END);
                braceletMessage.setManufacturerName(dataSplitList.get(0));
                braceletMessage.setDeviceId(dataSplitList.get(1));
                braceletMessage.setDataLen(dataSplitList.get(2));
                braceletMessage.setDataType(dataType);
                braceletMessage.setDataContent(dataContent);
                messageList.add(braceletMessage);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.warn("报文解析过程中出现失败，问题报文数据：{}", message);
            }
        }
        if (!CollectionUtils.isEmpty(messageList)) {
            log.debug("报文解析结果(json)：{}", JSON.toJSONString(messageList, SerializerFeature.WriteMapNullValue));
            // 根据协议文档对终端请求响应数据
            String messageResp = messageRespHandle(messageList);
            channelWrite(ctx.channel().id(), messageResp);
        }
    }

    private String messageRespHandle(List<BraceletMessage> messageList) {
        StringBuilder builderAll = new StringBuilder();
        StringBuilder builderSingle = new StringBuilder();
        for (BraceletMessage item : messageList) {
            builderSingle.append("[").append(item.getManufacturerName())
                    .append("*").append(item.getDeviceId()).append("*");
            // 匹配不同类型的处理逻辑
            switch (item.getDataType()) {
                case BraceletMessageTypeConstant.HEARTBEAT_KA:
                    builderSingle.append(String.format("%04X", BraceletMessageTypeConstant.HEARTBEAT_KA.length()))
                            .append("*").append(BraceletMessageTypeConstant.HEARTBEAT_KA).append("]");
                    break;
                case BraceletMessageTypeConstant.HEART:
                    builderSingle.append(String.format("%04X", BraceletMessageTypeConstant.HEART.length()))
                            .append("*").append(BraceletMessageTypeConstant.HEART).append("]");
                    break;
                case BraceletMessageTypeConstant.BLOOD:
                    builderSingle.append(String.format("%04X", BraceletMessageTypeConstant.BLOOD.length()))
                            .append("*").append(BraceletMessageTypeConstant.BLOOD).append("]");
                    break;
                default:
                    builderSingle = new StringBuilder();
                    break;
            }
            builderAll.append(builderSingle);
        }
        return builderAll.toString();
    }


    /*@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        byte[] bytes;
        try {
            bytes = "通道读数据完成".getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        ctx.writeAndFlush(buf);
    }*/

    /**
     * 服务端给客户端发送消息
     *
     * @param channelId 连接通道唯一id
     * @param msg       需要发送的消息内容
     */
    public void channelWrite(ChannelId channelId, String msg) throws Exception {
        Channel channel = ChannelMap.getChannelMap().get(channelId);
        if (channel == null) {
            log.debug("通道：{}不存在", channelId);
            return;
        }
        if (!StringUtils.hasText(msg)) {
            log.debug("服务端响应消息为空");
            return;
        }
        //将客户端的信息直接返回写入ctx
        //刷新缓存区
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
        log.debug("服务端响应客户端[{}]数据：{}", channel.id(), msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("Client:{},READER_IDLE 读超时", socketString);
                ctx.disconnect();
                Channel channel = ctx.channel();
                ChannelId id = channel.id();
                ChannelMap.removeChannelByName(id);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.debug("Client:{}, WRITER_IDLE 写超时", socketString);
                ctx.disconnect();
                Channel channel = ctx.channel();
                ChannelId id = channel.id();
                ChannelMap.removeChannelByName(id);
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.debug("Client:{},ALL_IDLE 总超时", socketString);
                ctx.disconnect();
                Channel channel = ctx.channel();
                ChannelId id = channel.id();
                ChannelMap.removeChannelByName(id);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 发生异常会触发此函数
     *
     * @param ctx   通道处理程序上下文
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.warn("客户端{}：发生了错误，此连接已被关闭；当前连通数量:{}", ctx.channel().id(), ChannelMap.getChannelMap().size());
        log.error(cause.getMessage(), cause);
    }

}