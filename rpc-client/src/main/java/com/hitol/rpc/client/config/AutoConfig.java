package com.hitol.rpc.client.config;

import com.hitol.rpc.client.InitRpc;
import com.hitol.rpc.client.bootstrap.NettyClientBootStrap;
import com.hitol.rpc.client.bootstrap.NettyServerBootStrap;
import com.hitol.rpc.client.handler.NettyClientHandler;
import com.hitol.rpc.client.handler.NettyClientSendHandler;
import com.hitol.rpc.client.handler.NettyServerHandler;
import com.hitol.rpc.framework.config.ServerConfig;
import com.hitol.rpc.framework.util.IPUtil;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class AutoConfig {

    @Value("${rpc.server.port}")
    private int serverPort;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public InitRpc init() {
        InitRpc initRpc = new InitRpc();
        return initRpc;
    }

    @Bean
    public ServerConfig serverConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(serverPort);
        return serverConfig;
    }

    @Bean
    public NettyClientSendHandler nettyClientSendHandler() {
        NettyClientSendHandler nettyClientSendHandler = new NettyClientSendHandler(applicationContext);
        return nettyClientSendHandler;
    }

    @Bean
    public NettyClientHandler nettyClientHandler(@Qualifier("nettyClientSendHandler") NettyClientSendHandler clientSendHandler) {
        NettyClientHandler nettyClientHandler = new NettyClientHandler(clientSendHandler);
        return nettyClientHandler;
    }

    @Bean
    public NettyServerHandler nettyServerHandler() {
        NettyServerHandler nettyServerHandler = new NettyServerHandler();
        return nettyServerHandler;
    }

    @SneakyThrows
    @Bean
    @DependsOn("nettyServerBootStrap")
    public NettyClientBootStrap nettyClientBootStrap(@Qualifier("nettyClientHandler") NettyClientHandler nettyClientHandler) {
        NettyClientBootStrap bootStrap = new NettyClientBootStrap(nettyClientHandler, IPUtil.localIp(), serverPort);
        bootStrap.start();
        return bootStrap;
    }

    @Bean
    public NettyServerBootStrap nettyServerBootStrap(@Qualifier("nettyServerHandler") NettyServerHandler nettyServerHandler) {
        NettyServerBootStrap bootStrap = new NettyServerBootStrap(nettyServerHandler, serverPort);
        new Thread(() -> {
            try {
                bootStrap.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return bootStrap;
    }
}
