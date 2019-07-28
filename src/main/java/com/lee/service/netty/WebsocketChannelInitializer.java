package com.lee.service.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebsocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();

//        // 这里处理http消息的编解码
//        channelPipeline.addLast("httpServerCodec", new HttpServerCodec());
//
//
//
//        channelPipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
//
//        channelPipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
//
////        channelPipeline.addLast("aggregator", new HttpObjectAggregator(65536));
//        //用于处理websocket, /ws为访问websocket时的uri
//        channelPipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
//        // 添加自定义的ChannelHanlder
//        channelPipeline.addLast("websocketHandler", new WebsocketHandler());



        socketChannel.pipeline().addLast("http-codec",new HttpServerCodec());//设置解码器
        socketChannel.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));//聚合器，使用websocket会用到
        socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//用于大数据的分区传输
        socketChannel.pipeline().addLast("handler",new NioWebsocketHandler());//自定义的业务handler

    }
}
