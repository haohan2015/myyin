package com.myyin.rpc.server;

import com.myyin.common.util.StringUtil;
import com.myyin.registry.ServiceDiscovery;
import com.myyin.registry.ServiceRegistry;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @Description: RPC 服务器（用于发布 RPC 服务）
 * @date 2019/4/16 21:50
 */
public class RpcServer implements ApplicationContextAware,InitializingBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系11
     */
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //扫描带有 RpcService 主键的类并初始化 handlerMap 对象
        Map<String,Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isNotEmpty(serviceBeanMap)){
            for (Object serviceBean:serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if(StringUtil.isNotEmpty(serviceVersion)){
                    serviceName += "-"+serviceVersion;
                }
                handlerMap.put(serviceName,serviceBean);
            }
        }
    }
}
