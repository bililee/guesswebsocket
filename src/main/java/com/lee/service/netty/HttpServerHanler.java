package com.lee.service.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * 这里是netty处理请求类
 */
public class HttpServerHanler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 重写下相关的请求处理方法
     * @param channelHandlerContext
     * @param fullHttpRequest
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        channelHandlerContext.channel().remoteAddress();

        FullHttpRequest request = fullHttpRequest;

        System.out.println("请求的方法名为" + request.method().name());

        System.out.println("uri" + request.uri());

        /**
         * ByteBuf 为存储了请求内容体
         */
        ByteBuf byteBuf = request.content();

        System.out.println("传输的内容为" + byteBuf.toString(CharsetUtil.UTF_8));

        ByteBuf responseBuf = Unpooled.copiedBuffer("Hello netty", CharsetUtil.UTF_8);

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, responseBuf);

        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");

        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, responseBuf.readableBytes());

        channelHandlerContext.writeAndFlush(fullHttpResponse);

    }
}
