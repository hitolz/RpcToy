package com.hitol.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.hitol.rpc.client.handler.NettyClientSendHandler;
import com.hitol.rpc.common.bean.Invocation;
import com.hitol.rpc.common.request.SyncRequest;
import com.hitol.rpc.common.response.RemoteResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcProxyFactory {

    NettyClientSendHandler sendHandler;
    Class clazz;

    private ConcurrentHashMap<ClassLoader, Map<Class, Object>> map = new ConcurrentHashMap<>();

    public RpcProxyFactory(NettyClientSendHandler sendHandler, Class clazz) {
        this.sendHandler = sendHandler;
        this.clazz = clazz;
    }

    public Object getInstance() {
        return map.getOrDefault(clazz.getClassLoader(), new HashMap<>()).getOrDefault(clazz, createProxy());
    }

    private Object createProxy() {
        Map<Class, Object> classObjectMap = map.getOrDefault(clazz.getClassLoader(), new HashMap<>());
        Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String requestId = UUID.randomUUID().toString();
                Invocation invocation = Invocation.builder()
                        .clazz(clazz)
                        .className(clazz.getName())
                        .methodName(method.getName())
                        .paramTypes(method.getParameterTypes())
                        .params(args)
                        .requestId(requestId)
                        .build();
                SyncRequest<Object> request = new SyncRequest<Object>();
                request.setRequestId(requestId);
                request.setRequest(invocation);

                RemoteResponse remoteResponse = sendHandler.sendSyncMsg(request);
                return remoteResponse;
            }
        });
        classObjectMap.put(clazz, proxyInstance);
        return proxyInstance;
    }
}
