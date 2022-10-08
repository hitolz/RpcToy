package com.hitol.rpc.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hitol.rpc.client.handler.NettyClientSendHandler;
import com.hitol.rpc.client.proxy.RpcProxyFactory;
import com.hitol.rpc.common.bean.RpcBeanDefinition;
import com.hitol.rpc.framework.annonation.RpcConsumer;
import com.hitol.rpc.framework.annonation.RpcProvider;
import com.hitol.rpc.framework.annonation.RpcReference;
import com.hitol.rpc.framework.annonation.RpcService;
import com.hitol.rpc.framework.config.ServerConfig;
import com.hitol.rpc.framework.util.IPUtil;
import com.hitol.rpc.registery.Registery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Slf4j
public class InitRpc implements InitializingBean, DisposableBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Registery registery;

    private ServerConfig serverConfig;

    private String localIp;
    private int serverPort;


    @Override
    public void destroy() throws Exception {
        // 销毁时发送消息到注册中心下线
        log.info("服务下线");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registery = Registery.getInstance();

        Assert.notNull(applicationContext, "Application must not be null!");

        this.serverConfig = applicationContext.getBean(ServerConfig.class);
        localIp = IPUtil.localIp();
        serverPort = serverConfig.getServerPort();

        findAndRegisterProvider();
        findAndRegisterConsumer();
    }


    private void findAndRegisterProvider() {
        Map<String, Object> providersMap = applicationContext.getBeansWithAnnotation(RpcProvider.class);
        if (CollectionUtils.isEmpty(providersMap)) {
            return;
        }
        log.info("注册服务提供者 ip = {},serverPort = {}", localIp, serverPort);
        registery.doRegisterProvider(localIp, serverPort);

        log.info("注册服务");
        Map<String, Object> rpcServicesMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(rpcServicesMap)) {
            return;
        }
        List<RpcBeanDefinition> providerServiceList = new ArrayList<>();
        List<Object> rpcServices = rpcServicesMap.entrySet().stream().map(x -> x.getValue()).collect(Collectors.toList());
        rpcServices.forEach(object -> {
            providerServiceList.addAll(convertToBeanDefinition(object));
        });

        providerServiceList.forEach(x -> {
            registery.doRegisterProviderApi(x);
        });
    }

    private List<RpcBeanDefinition> convertToBeanDefinition(Object bean) {
        List<RpcBeanDefinition> list = new ArrayList<>();
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        RpcBeanDefinition definition = null;
        for (Method method : methods) {
            method.setAccessible(true);
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            definition = new RpcBeanDefinition();
            definition.setBeanInstance(bean);
            definition.setClazz(clazz);
            definition.setClassName(clazz.getName());
            definition.setMethodName(method.getName());
            definition.setParamTypes(parameterTypes);
            list.add(definition);
        }
        return list;
    }

    private void findAndRegisterConsumer() {
        Map<String, Object> consumersMap = applicationContext.getBeansWithAnnotation(RpcConsumer.class);
        if (CollectionUtils.isEmpty(consumersMap)) {
            return;
        }
        registery.doRegisterConsumer(localIp, serverPort);

        Map<String, Object> componetsMap = applicationContext.getBeansWithAnnotation(Component.class);
        List<Object> componets = componetsMap.entrySet().stream().map(x -> x.getValue()).collect(Collectors.toList());

        componets.forEach(springBean -> {
            Class<?> springBeanClass = springBean.getClass();
            Field[] declaredFields = springBeanClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                RpcReference annotation = field.getAnnotation(RpcReference.class);
                if (annotation == null) {
                    continue;
                }
                Class<?> referenceClass = field.getType();

                Object proxy = getOrCreateProxy(referenceClass, springBeanClass, field);
                // 找到了注册中心中的 bean，通过反射将其塞到消费者的 bean 中
                try {
                    field.set(springBean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Object getOrCreateProxy(Class<?> referenceClass, Class<?> springBeanClass, Field field) {
        // 先默认服务即是提供者又是消费者，可以从 spring 容器中直接获取 服务提供者
        Object bean = applicationContext.getBean(referenceClass);
        RpcBeanDefinition providerBean = registery.getProviderBean(bean.getClass().getName());
        if (providerBean == null) {
            throw new RuntimeException("class = " + springBeanClass.getName() + ",fieldName = " + field.getName() + " 没有服务提供者");
        }
        NettyClientSendHandler sendHandler = applicationContext.getBean(NettyClientSendHandler.class);
        return new RpcProxyFactory(sendHandler, referenceClass).getInstance();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Registery getRegistery() {
        return registery;
    }

    public void setRegistery(Registery registery) {
        this.registery = registery;
    }
}


