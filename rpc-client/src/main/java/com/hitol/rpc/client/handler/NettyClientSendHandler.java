package com.hitol.rpc.client.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.hitol.rpc.client.bootstrap.NettyClientBootStrap;
import com.hitol.rpc.common.bean.Invocation;
import com.hitol.rpc.common.request.SyncRequest;
import com.hitol.rpc.common.response.RemoteResponse;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
public class NettyClientSendHandler {

    private final ConcurrentHashMap<String, SynchronousQueue<Object>> requests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SyncRequest> requestsMap = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    public NettyClientSendHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void sendMsg(String msg) {
        ChannelFuture channelFuture = getChannelFuture(msg);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    log.info("发送成功");
                } else {
                    log.info("发送失败");
                }
            }
        });
    }

    private ChannelFuture getChannelFuture(String msg) {
        ChannelFuture channelFuture = getSocketChannel().writeAndFlush(msg);
        return channelFuture;
    }

    private SocketChannel getSocketChannel() {
        SocketChannel socketChannel = applicationContext.getBean(NettyClientBootStrap.class).getSocketChannel();
        return socketChannel;
    }

    @SneakyThrows
    public RemoteResponse sendSyncMsg(SyncRequest<Object> syncRequest) {

        // 放入缓存中
        String requestId = syncRequest.getRequestId();
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        requests.put(requestId, queue);
        requestsMap.put(requestId, syncRequest);

        ChannelFuture channelFuture = getChannelFuture(syncRequest.getRequest());
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    log.info("发送成功");
                } else {
                    log.info("发送失败");
                }
            }
        });
        RemoteResponse response = (RemoteResponse) syncRequest.get(8, TimeUnit.SECONDS);
        //RemoteResponse response = (RemoteResponse) queue.poll(5, TimeUnit.SECONDS);
        return response;
    }

    private ChannelFuture getChannelFuture(Invocation invocation) {
        String msg = JSONObject.toJSONString(invocation);
        log.info("发送消息 {}", msg);
        ChannelFuture channelFuture = getSocketChannel().writeAndFlush(msg);
        return channelFuture;
    }

    @SneakyThrows
    public void ackSyncMsg(String msg) {
        log.info("ACK确认信息: {}", msg);
        RemoteResponse response = JSONObject.parseObject(msg, RemoteResponse.class);
        String requestId = response.getRequestId();

        SyncRequest syncRequest = requestsMap.get(requestId);
        if (syncRequest != null) {
            syncRequest.setResponse(response);
            requestsMap.remove(requestId);
        }

        // 从缓存中获取数据
        //SynchronousQueue<Object> queue = requests.get(requestId);
        //queue.put(response);
    }
}
