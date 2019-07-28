package com.lee;

import com.lee.service.netty.HttpServerInitializer;
import com.lee.service.netty.NioWebsocketHandler;
import com.lee.service.netty.WebsocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketServer {
    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childLoop = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(parentGroup, childLoop).channel(NioServerSocketChannel.class).childHandler(new WebsocketChannelInitializer());

//            serverBootstrap.group(parentGroup, childLoop).channel(NioServerSocketChannel.class).childHandler(new WebsocketChannelInitializer());

            ChannelFuture future = serverBootstrap.bind(9292).sync();

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
