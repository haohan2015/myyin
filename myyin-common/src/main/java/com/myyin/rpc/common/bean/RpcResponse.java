package com.myyin.rpc.common.bean;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/16 19:16
 */
public class RpcResponse {

    private String requestId;

    private Exception exception;

    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean hasException() {
        return exception != null;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }
}
