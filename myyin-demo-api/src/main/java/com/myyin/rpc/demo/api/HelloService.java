package com.myyin.rpc.demo.api;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 19:46
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);

}
