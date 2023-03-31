package com.example.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 服务端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调，设置出站编码器和入站解码器
 */
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 接收消息格式，使用自定义解析数据格式
        pipeline.addLast("decoder", new MyDecoder());
        // 发送消息格式，使用自定义解析数据格式
        pipeline.addLast("encoder", new MyEncoder());
        // 针对客户端，如果在规定时间内没有向服务端发送写心跳(ALL)，则主动断开
        // 如果是读空闲或者写空闲，不处理,这里根据自己业务考虑使用
        // 此处参数表示：只针对读空闲心跳检测，如果超过5分钟没有读到数据则表示断开连接
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.MINUTES));
        // 自定义的服务器处理程序
        pipeline.addLast(new NettyServerHandler());
    }

    /**
     * 自定义入站解码器
     */
    static class MyDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) {
            // 创建字节数组，buffer.readableBytes可读字节长度
            byte[] b = new byte[byteBuf.readableBytes()];
            // 复制内容到字节数组b
            byteBuf.readBytes(b);
            // 字节数组转字符串
            String str = new String(b, CharsetUtil.UTF_8);
            out.add(str);
        }
    }

    /**
     * 自定义出站编码器
     */
    static class MyEncoder extends MessageToByteEncoder<String> {

        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, String msg, ByteBuf byteBuf) {
            byteBuf.writeBytes(msg.getBytes());
        }
    }
}
