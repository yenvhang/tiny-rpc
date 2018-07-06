package client;

import top.nvhang.rpc.core.BIOClient;
import top.nvhang.rpc.core.Client;
import top.nvhang.rpc.server.HelloServer;
import top.nvhang.rpc.registry.ZKServiceDiscovery;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ClientMain {
    private static ExecutorService executorService = Executors.newFixedThreadPool(20);
    public static void main(String[] args) {
        // 新建一个 服务发现 对象
        ZKServiceDiscovery zkServiceDiscovery =new ZKServiceDiscovery();
        //设置获取服务算法 为 一致性hash 算法
        zkServiceDiscovery.setStrategy(ZKServiceDiscovery.CONSISTENT_HASH_STRATEGY);
        BIOClient bioClient =new BIOClient();
        // 为客户端 设置 服务发现 对象
        bioClient.setServiceDiscovery(zkServiceDiscovery);

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
