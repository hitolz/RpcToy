package com.hitol.rpctoyexample;

import com.hitol.rpc.framework.annonation.RpcConsumer;
import com.hitol.rpc.framework.annonation.RpcProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RpcProvider
@RpcConsumer
public class RpcToyExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcToyExampleApplication.class, args);
    }

}
