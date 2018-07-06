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
        // �½�һ�� ������ ����
        ZKServiceDiscovery zkServiceDiscovery =new ZKServiceDiscovery();
        //���û�ȡ�����㷨 Ϊ һ����hash �㷨
        zkServiceDiscovery.setStrategy(ZKServiceDiscovery.CONSISTENT_HASH_STRATEGY);
        BIOClient bioClient =new BIOClient();
        // Ϊ�ͻ��� ���� ������ ����
        bioClient.setServiceDiscovery(zkServiceDiscovery);

        //��ȡ�������
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
