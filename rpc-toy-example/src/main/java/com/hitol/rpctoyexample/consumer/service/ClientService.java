package com.hitol.rpctoyexample.consumer.service;

import com.hitol.rpc.common.response.RemoteResponse;
import com.hitol.rpc.framework.annonation.RpcReference;
import com.hitol.rpctoyexample.provider.service.IHelloService;

import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @RpcReference
    private IHelloService helloService;

    public RemoteResponse test() {
        RemoteResponse msg = helloService.sayHello("lisi");
        System.out.println(msg);
        return msg;
    }

}
