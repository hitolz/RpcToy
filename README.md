# 基于 Netty 模拟实现 RPC

实现 RPC 框架选择 Netty 实在是在合理不过了，Dubbo 的底层通信协议也是使用了 Netty。

用 Netty 实现 RPC 都有哪些方面呢

- [ ] 注册中心，暂时使用 redis
- [x] rpc-client,一个 jar 包，提供注解 @RpcProvider，用在服务提供方，@RpcConsumer 用在消费方，两个注解用在 Spring
  启动类上，表示该应用是
  Rpc 服务提供者或消费者。
- [x]  @RpcService，表明是一个服务端的服务
    - [x]  在 Spring 启动的同时，将该服务注册到注册中心
- [x] @RpcReference，表明是一个消费者要使用服务端的服务
    - [ ] 在 Spring 启动的时候，将服务端提供的 class 通过动态代理注入到使用注解的类属性上
- [ ] 心跳模块，客户端和服务端定时发送心跳到注册中心
- [ ]  demo
- [x] 同一个 spring 应用即是服务提供者又是服务消费者
    - [ ] 不同 spring 应用
    - [ ] 不使用 spring
- [ ] 性能测试

---
https://juejin.cn/post/7079780597691400206
