package com.lee.service.netty;

import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;


public class NioWebsocketHandler extends SimpleChannelInboundHandler<Object> {


    private ChannelSupervise channelSupervise;

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("收到相关的信息");
        if (o instanceof FullHttpRequest) {
            this.handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
            // 处理http的请求
        }
        if (o instanceof WebSocketFrame) {
            // 处理websocket客户端的信息
            handlerWebsockFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    /**
     * 当有新的连接加入的时候
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelSupervise.addChannel(ctx.channel());
//        super.channelActive(ctx);
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void handlerWebsockFrame(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            // 关闭
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
            return;
        }
        if (webSocketFrame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(webSocketFrame.content().retain())
            );
            return;
        }
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            System.out.println("不支持此类文本信息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", webSocketFrame.getClass().getName()));
        }
        String request = ((TextWebSocketFrame) webSocketFrame).text();
        System.out.println("服务端收到的信息：" + request);
        TextWebSocketFrame txt = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "来啦");
        channelSupervise.send2All(txt);
        // 如果是要返回给特定的人（谁发的就还给谁）
        ctx.channel().writeAndFlush(txt);
    }

    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:9292/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     * */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        //……
        if(channel.isActive())ctx.close();
    }
}
