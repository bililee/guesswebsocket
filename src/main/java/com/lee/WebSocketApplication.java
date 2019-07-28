package com.lee;

import com.lee.service.netty.HttpServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketApplication {

    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childLoop = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(parentGroup).channel(NioServerSocketChannel.class).childHandler(new HttpServerInitializer());

            ChannelFuture future = serverBootstrap.bind(9090).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException exp) {
            exp.printStackTrace();
        } finally {
            // 这里关闭下相关的线程池

            parentGroup.shutdownGracefully();

            childLoop.shutdownGracefully();
        }
    }
}
