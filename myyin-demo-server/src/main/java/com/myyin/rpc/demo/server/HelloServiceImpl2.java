package com.myyin.rpc.demo.server;

import com.myyin.rpc.demo.api.HelloService;
import com.myyin.rpc.demo.api.Person;
import com.myyin.rpc.server.RpcService;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 19:53
 */
@RpcService(value = HelloService.class,version = "demo.hello2")
public class HelloServiceImpl2 implements HelloService{
    @Override
    public String hello(String name) {
        return "你好 "+ name;
    }

    @Override
    public String hello(Person person) {
        return "你好 " + person.getFirstName() + " " + person.getLastName();
    }
}
