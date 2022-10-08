package com.hitol.rpc.client.config;

import io.netty.channel.socket.SocketChannel;

public class SocketChannelCache {

    private SocketChannel socketChannel;

    public SocketChannel getSocketChannel() {
        if (socketChannel == null) {
            
        }
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
