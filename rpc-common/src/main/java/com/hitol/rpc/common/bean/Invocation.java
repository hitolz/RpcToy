package com.hitol.rpc.common.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invocation implements Serializable {

    private static final long serialVersionUID = 7044032004756437127L;
    private String className;
    private Class clazz;
    private String methodName;
    private Class[] paramTypes;
    private Object[] params;

    private String requestId;
}
