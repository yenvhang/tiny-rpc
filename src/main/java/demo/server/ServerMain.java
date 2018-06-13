package demo.server;

import core.ServerCenter;
import demo.server.HelloServer;
import demo.server.HelloServerImpl;

import java.io.IOException;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ServerMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ServerCenter serverCenter =new ServerCenter(8090);
        serverCenter.registry(HelloServer.class, HelloServerImpl.class);
        serverCenter.start();
    }
}
