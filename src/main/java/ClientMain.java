import server.HelloServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ClientMain {
    private static ExecutorService executorService = Executors.newFixedThreadPool(20);
    public static void main(String[] args) {

        //获取代理对象
        HelloServer helloServer =Client.getProxyObject(HelloServer.class,8090);
        for(int i=0;i<=100;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    helloServer.sayHello( "proxy test threadId:"+ Thread.currentThread());
                }
            });
        }


    }
}
