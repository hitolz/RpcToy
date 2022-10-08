package com.hitol.rpc.registery;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hitol.rpc.common.bean.RpcBeanDefinition;

public class Registery {

    public static Map<String, Map<String, RpcBeanDefinition>> map = new HashMap<>();
    public static Map<String, RpcBeanDefinition> beanDefinitionMap = new HashMap<>();

    private static Registery registery = new Registery();

    private Registery() {
    }

    public static Registery getInstance() {
        return registery;
    }

    public void doRegisterProvider(String localIp, int serverPort) {
        System.out.printf("【注册中心】注册服务提供者 ip = %s,serverPort = %d \n", localIp, serverPort);
    }

    public void doRegisterConsumer(String localIp, int serverPort) {
        System.out.printf("【注册中心】注册服务消费者 ip = %s,serverPort = %d \n", localIp, serverPort);

    }

    public void doRegisterProviderApi(RpcBeanDefinition beanDefinition) {
        // lock、去重
        System.out.printf("【注册中心】注册api %s \n", JSONObject.toJSONString(beanDefinition));
        String className = beanDefinition.getClassName();
        String methodName = beanDefinition.getMethodName();

        beanDefinitionMap.put(className, beanDefinition);

        Map<String, RpcBeanDefinition> beanDefinitionMap = map.getOrDefault(className, new HashMap<>());
        beanDefinitionMap.put(methodName, beanDefinition);
        map.put(className, beanDefinitionMap);
    }

    public RpcBeanDefinition getProviderBean(String className) {
        return beanDefinitionMap.getOrDefault(className, null);
    }
}
