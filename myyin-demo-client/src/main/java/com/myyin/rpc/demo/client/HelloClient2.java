package com.myyin.rpc.demo.client;

import com.myyin.rpc.client.RpcProxy;
import com.myyin.rpc.demo.api.HelloService;
import com.myyin.rpc.demo.api.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 21:07
 */
public class HelloClient2 {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("my", "yin"));
        System.out.println(result);

        System.exit(0);

    }
}
