package com.hitol.rpc.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcBeanDefinition {
    private Object beanInstance;
    private Class clazz;
    private String className;

    private String methodName;

    private Class[] paramTypes;
    private Object[] params;

}
