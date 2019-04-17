package com.myyin.rpc.demo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/17 19:57
 */
public class RpcBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcBootstrap.class);

    public static void main(String[] args) {

        LOGGER.debug("start server");
        new ClassPathXmlApplicationContext("spring.xml");

    }

}
