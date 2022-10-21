package com.hitol.rpcexample.provider.service.impl;

import com.hitol.rpc.common.response.RemoteResponse;
import com.hitol.rpc.framework.annonation.RpcService;
import com.hitol.rpctoyexample.provider.service.IHelloService;

import org.springframework.stereotype.Service;

@Service
@RpcService
public class HelloServiceImpl implements IHelloService {
    @Override
    public RemoteResponse sayHello(String name) {
        return RemoteResponse.success("Hello " + name + ",this is Tom");
    }

    public void test() {
        System.out.println(1);
    }
}
