package com.hitol.rpcexample.consumer.controller;

import com.hitol.rpc.common.response.RemoteResponse;
import com.hitol.rpcexample.consumer.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    ClientService clientService;

    @RequestMapping("/")
    public RemoteResponse test() {
        RemoteResponse msg = clientService.test();
        return msg;
    }
}
