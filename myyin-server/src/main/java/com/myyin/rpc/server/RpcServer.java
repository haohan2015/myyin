package com.myyin.rpc.server;

import com.myyin.rpc.common.bean.RpcRequest;
import com.myyin.rpc.common.bean.RpcResponse;
import com.myyin.rpc.common.codec.RpcDecoder;
import com.myyin.rpc.common.codec.RpcEncoder;
import com.myyin.rpc.common.util.StringUtil;
import com.myyin.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{

            //创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 解码 RPC 请求
                            pipeline.addLast(new RpcDecoder(RpcRequest.class));

                            //编码 RPC 响应
                            pipeline.addLast(new RpcEncoder(RpcResponse.class));

                            //处理 RPC 请求
                            pipeline.addLast(new RpcServerHandler(handlerMap));
                        }
                    });

            bootstrap.option(ChannelOption.SO_BACKLOG,2014);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);

            //获取 RPC 服务器的 IP 地址端口
            String[] addressArray = StringUtil.split(serviceAddress,":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            //启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(ip,port).sync();
            //注册 RPC 服务地址
            if(serviceRegistry != null){
                for (String interfaceName:handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName,serviceAddress);
                    LOGGER.debug("register service:{}=>{}",interfaceName,serviceAddress);
                }
            }
            LOGGER.debug("server started on port{}",port);
            //关闭 RPC 服务器
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
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
