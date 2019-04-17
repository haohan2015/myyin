package com.myyin.rpc.registry;

/**
 * @author admin
 * @Description: 服务注册接口
 * @date 2019/4/16 19:18
 */
public interface ServiceRegistry {

    /**
     * 注册服务名称与服务地址
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName,String serviceAddress);
}
