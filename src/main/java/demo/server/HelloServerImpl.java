package demo.server;

import core.domain.RPCServer;

/**
 * Created by yeyh on 2018/6/12.
 */
@RPCServer(HelloServer.class)
public class HelloServerImpl implements HelloServer {
    @Override
    public String sayHello(String world) {
        System.out.println("hello~ " +world );
        return world;
    }
}
