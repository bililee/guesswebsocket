package com.lee.service.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;


public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel = channelHandlerContext.channel();
        System.out.println(channel.remoteAddress() + ": " + textWebSocketFrame.text());

        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("来自服务端: " + new Date().toString()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelId" + ctx.channel().id().asLongText());
//        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户下线：" + ctx.channel().id().asLongText());
//        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }



}
