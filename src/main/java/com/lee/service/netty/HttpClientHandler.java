package com.lee.service.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    /**
     * 这里是构造一个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        URI uri = new URI("http://127.0.0.1:9090");
        String transferMsg = "Are you ok?";
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                uri.toASCIIString(),
                Unpooled.wrappedBuffer(transferMsg.getBytes("UTF-8"))
        );
        System.out.println("in active");
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        ctx.channel().writeAndFlush(request);

    }


    /**
     * 这里处理请求返回的数据
     * @param channelHandlerContext
     * @param fullHttpResponse
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse fullHttpResponse) throws Exception {
        FullHttpResponse response = fullHttpResponse;
        response.headers().get(HttpHeaderNames.CONTENT_TYPE);
        ByteBuf returnResponse = response.content();
        System.out.println(returnResponse.toString(CharsetUtil.UTF_8));
    }
}
