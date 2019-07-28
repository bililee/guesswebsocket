package com.lee.service.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelSupervise {
    /**
     * 这里之所以要定义两个，一个channelGroup 和 一个map的原因是channelid不是我们自己生成的，所以很难对应一个用户与他原先自己的id
     * 所以这里加了一个map 用来保存用户的id 与 channelid的对应关系
     */

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static ConcurrentHashMap<String, ChannelId> channelMap = new ConcurrentHashMap<String, ChannelId>();

    public static void addChannel(Channel channel) {
        channelGroup.add(channel);
        channelMap.put(channel.id().asShortText(), channel.id());
    }

    public static void removeChannel(Channel channel) {
        channelGroup.remove(channel);
        channelMap.remove(channel.id().asShortText());
    }

    public static Channel findChannel(String id) {
        return channelGroup.find(channelMap.get(id));
    }

    public static void send2All(TextWebSocketFrame e) {
        channelGroup.writeAndFlush(e);
    }
}
