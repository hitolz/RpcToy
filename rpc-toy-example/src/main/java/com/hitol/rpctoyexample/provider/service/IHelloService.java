package com.hitol.rpctoyexample.provider.service;

import com.hitol.rpc.common.response.RemoteResponse;

public interface IHelloService {
    RemoteResponse sayHello(String name);
}
