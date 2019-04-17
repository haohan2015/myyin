package com.myyin.rpc.demo.server;

import com.myyin.rpc.demo.api.HelloService;
import com.myyin.rpc.demo.api.Person;
import com.myyin.rpc.server.RpcService;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 19:52
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello " + person.getFirstName() + " " + person.getLastName();
    }
}
