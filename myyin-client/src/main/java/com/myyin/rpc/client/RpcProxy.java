package com.myyin.rpc.client;

import com.myyin.common.bean.RpcRequest;
import com.myyin.common.bean.RpcResponse;
import com.myyin.common.util.StringUtil;
import com.myyin.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author admin
 * @Description: RPC 代理（用于创建 RPC 服务代理）
 * @date 2019/4/16 20:20
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass){
        return create(interfaceClass,"");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass,final String serviceVersion){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
                rpcRequest.setServiceVersion(serviceVersion);
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameters(method.getParameterTypes());
                rpcRequest.setParameters(args);
                /**
                 * 获取 RPC 服务地址
                 */
                if(serviceDiscovery != null){
                    String serviceName = interfaceClass.getName();
                    if(StringUtil.isNotEmpty(serviceVersion)){
                        serviceName += "-" + serviceVersion;
                    }
                    serviceAddress = serviceDiscovery.discover(serviceName);
                    LOGGER.debug("discover server:{} => {}",serviceName,serviceAddress);
                }

                if(StringUtil.isEmpty(serviceAddress)){
                    throw new RuntimeException("server address is empty");
                }

                String[] array = StringUtil.split(serviceAddress,":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);

                /**
                 * 创建 RPC 客户端对并且发送RPC 请求
                 */
                RpcClient client = new RpcClient(host,port);
                long time = System.currentTimeMillis();
                RpcResponse response = client.send(rpcRequest);
                LOGGER.debug("time:{}ms",System.currentTimeMillis() - time);
                if(response == null){
                    throw new RuntimeException("response is null");
                }

                //处理 RPC 响应结果
                if(response.hasException()){
                    throw  response.getException();
                }else{
                    return response;
                }
            }
        });
    }
}
