package com.lee.service.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();

        // 这里处理http消息的编解码
        channelPipeline.addLast("httpServerCodec", new HttpServerCodec());

        channelPipeline.addLast("aggregator", new HttpObjectAggregator(65536));

        // 添加自定义的ChannelHanlder
        channelPipeline.addLast("httpServerHanlder", new HttpServerHanler());


    }
}
