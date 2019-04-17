package com.myyin.rpc.registry.zookeeper;

import com.myyin.rpc.common.util.CollectionUtil;
import com.myyin.rpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author admin
 * @Description: 基于 ZooKeeper 的服务发现接口
 * @date 2019/4/16 19:52
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String serviceName) {
        /**
         * zooKeeper客户端
         */
        ZkClient zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try{

            /**
             * 获取服务节点
             */
            String servercePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if(!zkClient.exists(servercePath)){
                throw new RuntimeException(String.format("can not find an service node on path: %s",servercePath));
            }

            /**
             * 获取服务列表
             */
            List<String> addressList = zkClient.getChildren(servercePath);
            if(CollectionUtil.isEmpty(addressList)){
                throw new RuntimeException(String.format("can not find any address node on path: %s",servercePath));
            }

            /**
             * 获取 address 节点
             */
            String address;
            int size = addressList.size();
            if(size == 1){

                /**
                 * 若只有一个地址，则获取该地址
                 */
                address = addressList.get(0);
                LOGGER.debug("get only address node: {}",address);
            }else{

                /**
                 * 若有多地址，则随机获取一个地址
                 */
                address = addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
                LOGGER.debug("get random address node:{}",address);
            }
            String addressPath = servercePath + "/" + address;
            return zkClient.readData(addressPath);

        }finally {
            zkClient.close();
        }
    }
}
