package com.myyin.rpc.demo.client;

import com.myyin.rpc.demo.api.HelloService;
import com.myyin.rpc.client.RpcProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 20:10
 */
public class HelloClient {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println("result = [" + result + "]");

        HelloService helloService1 = rpcProxy.create(HelloService.class,"demo.hello2");
        String result1 = helloService1.hello("世界");
        System.out.println("result1 = [" + result1 + "]");

        System.exit(0);

    }
}
