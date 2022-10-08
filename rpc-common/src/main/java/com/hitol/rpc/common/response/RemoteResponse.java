package com.hitol.rpc.common.response;

import lombok.Data;

@Data
public class RemoteResponse<T> {

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;
    private int code;
    private String error;
    private T data;
    private String requestId;


    public RemoteResponse() {
    }

    public static <T> RemoteResponse<T> success() {
        RemoteResponse<T> response = new RemoteResponse();
        response.setCode(SUCCESS_CODE);
        return response;
    }

    public static <T> RemoteResponse<T> success(T data) {
        RemoteResponse<T> response = new RemoteResponse();
        response.setCode(SUCCESS_CODE);
        response.setData(data);
        return response;
    }

    public static <T> RemoteResponse<T> success(T data, String requestId) {
        RemoteResponse<T> response = success(data);
        response.setRequestId(requestId);
        return response;
    }

    public static <T> RemoteResponse<T> error(String errorMessage) {
        return error(errorMessage, ERROR_CODE);
    }

    public static <T> RemoteResponse<T> error(String errorMessage, int errorCode) {
        RemoteResponse<T> response = new RemoteResponse();
        response.setCode(errorCode);
        response.setError(errorMessage);
        return response;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

}
