package com.lee;

import com.lee.service.netty.HttpClientHandler;
import com.lee.service.netty.HttpServerHanler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 这里是作为netty的客户端进行使用
 */
public class NettyClientApplication {
    public static void main(String[] args) {

        String host = "127.0.0.1";
        Integer port = 9090;

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("come in");
                            // 这里直接就定义一下吧
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 这里处理http消息的编解码
                            pipeline.addLast(new HttpClientCodec());

                            pipeline.addLast(new HttpObjectAggregator(65536));

                            // 添加自定义的ChannelHanlder
                            pipeline.addLast( new HttpClientHandler());

                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException exp) {
            // 俘获一下异常中断时候的处理
            exp.printStackTrace();

        }
        finally {
            System.out.println("end");
            group.shutdownGracefully();
        }

    }
}
