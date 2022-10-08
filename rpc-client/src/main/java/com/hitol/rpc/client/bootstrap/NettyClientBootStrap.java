package com.hitol.rpc.client.bootstrap;


import com.hitol.rpc.client.handler.NettyClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientBootStrap {

    private String host = "localhost";
    private int serverPort;
    private SocketChannel socketChannel = null;

    private NettyClientHandler nettyClientHandler;


    public NettyClientBootStrap(NettyClientHandler nettyClientHandler,
                                String host,
                                int serverPort) {
        this.nettyClientHandler = nettyClientHandler;
        this.host = host;
        this.serverPort = serverPort;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host, serverPort);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                        4, 0, 4));
                socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                socketChannel.pipeline().addLast(nettyClientHandler);
            }
        });
        ChannelFuture future = bootstrap.connect(host, serverPort).sync();
        if (future.isSuccess()) {
            socketChannel = (SocketChannel) future.channel();
            log.info("Netty client connect server success,ip = {},port = {}", host, serverPort);
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

}