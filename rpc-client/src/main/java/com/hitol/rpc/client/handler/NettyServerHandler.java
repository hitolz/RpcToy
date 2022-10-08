package com.hitol.rpc.client.handler;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.hitol.rpc.common.bean.Invocation;
import com.hitol.rpc.common.response.RemoteResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @param ctx
     * @return
     * @Description 客户端断开连接时执行，将客户端信息从Map中移除
     **/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接：{}", getClientIp(ctx.channel()));
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    /**
     * @param ctx
     * @return
     * @Description 客户端连接时执行，将客户端信息保存到Map中
     **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的客户端连接：{}", getClientIp(ctx.channel()));
        String clientIp = getClientIp(ctx.channel());
        NettyClient client = new NettyClient((SocketChannel) ctx.channel(), getClientIp(ctx.channel()));
        NettyChannelMap.add(clientIp, client);
    }

    /**
     * @param ctx
     * @param msg
     * @return
     * @Description 收到消息时执行，根据消息类型做不同的处理
     **/
    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到客户端消息：" + msg);
        Invocation invocation = JSONObject.parseObject(msg, Invocation.class);

        Class clazz = invocation.getClazz();
        String methodName = invocation.getMethodName();
        Class[] paramTypes = invocation.getParamTypes();
        Object[] params = invocation.getParams();
        String requestId = invocation.getRequestId();

        Method method = clazz.getMethod(methodName, paramTypes);
        RemoteResponse response = (RemoteResponse) method.invoke(applicationContext.getBean(clazz), params);
        response.setRequestId(requestId);
        log.info("返回信息 reponse = {}", response);
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
    }

    /**
     * @param ctx
     * @param cause
     * @description: TODO
     * @return: void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("抛出异常执行，包括客户端断开连接时，会抛出IO异常");
        cause.printStackTrace();
    }


    /**
     * @param channel
     * @return
     * @Description 获取客户端IP
     **/
    private String getClientIp(Channel channel) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        return clientIP;
    }

    /**
     * @Description 当有新的客户端连接的时候，用于保存客户端信息
     * @return
     **/
    public static class NettyChannelMap {

        public static Map<String, NettyClient> map = new ConcurrentHashMap<>();

        public static void add(String clientId, NettyClient client) {
            map.put(clientId, client);
        }

        public static NettyClient get(String clientId) {
            return map.get(clientId);
        }

        public static void remove(SocketChannel socketChannel) {
            for (Map.Entry entry : map.entrySet()) {
                if (((NettyClient) entry.getValue()).getChannel() == socketChannel) {
                    map.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * @Description 封装客户端的信息
     * @return
     **/
    @Data
    public static class NettyClient {

        /**
         * 客户端与服务器的连接
         */
        private SocketChannel channel;

        /**
         * ip地址
         */
        private String clientIp;

        // ......

        public NettyClient(SocketChannel channel, String clientIp) {
            this.channel = channel;
            this.clientIp = clientIp;
        }

    }

}