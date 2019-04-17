package com.myyin.rpc.client;

import com.myyin.rpc.common.bean.RpcRequest;
import com.myyin.rpc.common.bean.RpcResponse;
import com.myyin.rpc.common.codec.RpcDecoder;
import com.myyin.rpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author admin
 * @Description: RPC 客户端（用于发送 RPC 请求）
 * @date 2019/4/16 20:34
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final String host;

    private final int port;

    private RpcResponse rpcResponse;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception",cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try{

            /**
             * 用于初始化 Netty 客户端 Bootstrap 对象
             */
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //编码 RPC 请求
                            pipeline.addLast(new RpcEncoder(RpcRequest.class));
                            //解码 RPC 响应
                            pipeline.addLast(new RpcDecoder(RpcResponse.class));
                            //处理 RPC 响应
                            pipeline.addLast(RpcClient.this);
                        }
                    });
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            //连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(host,port).sync();
            //写入 RPC 请求数据并且关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();

            return rpcResponse;

        }finally {
            group.shutdownGracefully();
        }
    }
}
