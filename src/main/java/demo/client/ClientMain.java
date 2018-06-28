package demo.client;

import core.Client;
import core.BIOClient;
import core.ServiceDiscover;
import demo.server.HelloServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ClientMain {
    private static ExecutorService executorService = Executors.newFixedThreadPool(20);
    public static void main(String[] args) {
        BIOClient bioClient =new BIOClient();
        ServiceDiscover serviceDiscover =new ServiceDiscover(bioClient);
        serviceDiscover.discover();
        //获取代理对象
        HelloServer helloServer = Client.getProxyObject(HelloServer.class,bioClient);
        for(int i=0;i<10;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(helloServer.sayHello( "proxy test threadId:"+ Thread.currentThread()));
                }
            });
        }
    }
}
