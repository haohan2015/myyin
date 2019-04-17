package com.myyin.rpc.registry;

/**
 * @author admin
 * @Description: 服务发现接口
 * @date 2019/4/16 19:19
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务地址
     * @param serviceName 服务名称
     * @return 服务地址
     */
    String discover(String serviceName);

}
