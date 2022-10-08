package com.hitol.rpc.framework.annonation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用在接口上、类上或者方法上
 * 用在接口上、类上，将该类的所有 public 方法注册到注册中心
 * 用在方法上则只将该方法注册到注册中心
 *
 * @Author: tanhai
 * @Date: 2022/10/6 20:58
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcService {
    /**
     * 唯一标识服务，可以自定义，如果没有自定义，则默认取接口名或者方法名
     */
    String code() default "";
}
