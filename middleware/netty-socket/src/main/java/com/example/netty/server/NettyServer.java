package com.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 功能描述: netty服务启动类
 */
@Slf4j
@Component
public class NettyServer {

    public void start(InetSocketAddress address) {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 绑定线程池,编码解码
            // 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    // 使用NioServerSocketChannel作为服务器Channel的实现
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口设置套接字地址
                    .localAddress(address)
                    // 使用自定义处理类
                    .childHandler(new NettyServerChannelInitializer())
                    // 初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的,所以同一时间只能处理一个客户端连接。
                    // 多个客户端同时来的时候,服务端将不能处理的客户端连接请求放在队列中等待处理
                    // 服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 保持长连接，2小时无数据激活心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 将小的数据包包装成更大的帧进行传送，提高网络的负载
                    .childOption(ChannelOption.TCP_NODELAY, true);
            // 启动服务器(并绑定端口)，bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture future = bootstrap.bind(address).sync();
            // 给cf注册监听器，监听我们关心的事件
            future.addListener((ChannelFutureListener) cf -> {
                if (cf.isSuccess()) {
                    log.info("netty服务器启动成功，开始监听：{}:{}", address.getAddress(), address.getPort());
                } else {
                    log.warn("netty服务器启动失败，请检查ip地址端口是否正确：{}:{}", address.getAddress(), address.getPort());
                }
            });
            // 等待服务端监听端口关闭，closeFuture是异步操作
            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成，内部调用的是Object的wait()方法
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            // e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}