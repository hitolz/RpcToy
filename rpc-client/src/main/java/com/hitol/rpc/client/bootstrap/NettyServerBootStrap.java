package com.hitol.rpc.client.bootstrap;


import com.hitol.rpc.client.handler.NettyServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerBootStrap {

    private NettyServerHandler nettyServerHandler;

    private int serverPort;

    public NettyServerBootStrap(NettyServerHandler nettyServerHandler, int serverPort) {
        this.nettyServerHandler = nettyServerHandler;
        this.serverPort = serverPort;
    }

    public void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 使消息立即发出去，不用等待到一定的数据量才发出去
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 保持长连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

                            // 这里将LengthFieldBasedFrameDecoder添加到pipeline的首位，因为其需要对接收到的数据
                            // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
                            //LengthFieldBasedFrameDecoder 会按照参数指定的包长度偏移量数据对接收到的数据进行解码，从而得到目标消息体数据；而 LengthFieldPrepender 则会在响应的数据前面添加指定的字节数据，这个字节数据中保存了当前消息体的整体字节数据长度
                            //maxFrameLength：指定了每个包所能传递的最大数据包大小；
                            //lengthFieldOffset：指定了长度字段在字节码中的偏移量；
                            //lengthFieldLength：指定了长度字段所占用的字节长度；
                            //lengthAdjustment：对一些不仅包含有消息头和消息体的数据进行消息头的长度的调整，这样就可以只得到消息体的数据，这里的 lengthAdjustment 指定的就是消息头的长度；
                            //initialBytesToStrip：对于长度字段在消息头中间的情况，可以通过 initialBytesToStrip 忽略掉消息头以及长度字段占用的字节。
                            p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            p.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            p.addLast(nettyServerHandler);
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(serverPort).sync();
            if (f.isSuccess()) {
                log.info("Netty Server Start successful,port = {}", serverPort);
            } else {
                log.error("Netty Server Start failed");
            }
            // 等待服务监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 退出，释放线程资源
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

}