package com.hitol.rpc.framework.annonation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解表明要使用服务提供者提供的服务
 * 不一定要像 dubbo 一样要依赖服务方提供的 api jar包
 * 可以通过 code 来唯一标识服务
 *
 * @Author: tanhai
 * @Date: 2022/10/6 20:58
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface RpcReference {

    /**
     * 服务唯一标识 code
     */
    String code() default "";
}
