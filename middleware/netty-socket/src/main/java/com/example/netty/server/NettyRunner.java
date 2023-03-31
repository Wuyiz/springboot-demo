package com.example.netty.server;

import com.example.netty.server.config.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class NettyRunner implements CommandLineRunner {
    @Resource
    private NettyServer nettyServer;
    @Resource
    private SocketProperties socketProperties;

    @Override
    public void run(String... args) throws Exception {
        log.info("netty服务器正在启动，服务器地址：{}:{}", socketProperties.getHost(), socketProperties.getPort());
        InetSocketAddress address = new InetSocketAddress(socketProperties.getHost(), socketProperties.getPort());
        nettyServer.start(address);
    }
}
