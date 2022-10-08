//package com.hitol.rpc.client;
//
//
//import com.hitol.rpc.client.bootstrap.NettyClientBootStrap;
//
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
//
//    @Autowired
//    private NettyClientBootStrap bootStrap;
//
//    @SneakyThrows
//    @Override
//    @Async
//    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        bootStrap.start();
//    }
//}
