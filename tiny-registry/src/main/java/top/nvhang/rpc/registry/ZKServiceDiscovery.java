package top.nvhang.rpc.registry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import top.nvhang.rpc.registry.strategy.ConsistentHashStrategy;
import top.nvhang.rpc.registry.strategy.LoadBalancingStrategy;
import top.nvhang.rpc.registry.strategy.RandomStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yeyh on 2018/6/28.
 */
public class ZKServiceDiscovery implements ServiceDiscovery {

    /**
     * 一致性hash 算法
     */
    public static final LoadBalancingStrategy CONSISTENT_HASH_STRATEGY=new ConsistentHashStrategy();
    /**
     * 随机算法
     */
    public static final LoadBalancingStrategy RANDOM_STRATEGY=new RandomStrategy();


    private LoadBalancingStrategy strategy ;

    public ZKServiceDiscovery() {
        strategy=new RandomStrategy();
    }

    public ZKServiceDiscovery(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }


    private CountDownLatch countDownLatch =new CountDownLatch(1);



    private ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {

             zooKeeper = new ZooKeeper("121.196.216.17:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }


    public void discover(){
        ZooKeeper zooKeeper =connect();
        Stat stat =new Stat();
        if(zooKeeper!=null){
            try {
                List<String> nodes =zooKeeper.getChildren(Constant.RPC_REGISTRY_PATH, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if(event.getType()==Event.EventType.NodeDataChanged){
                            discover();
                        }
                    }
                },stat);
                //todo 不应该clear 还会有并发问题 待优化
                strategy.clear();

                for(String node: nodes){
                    String url =new String(zooKeeper.getData(Constant.RPC_REGISTRY_PATH+"/"+node,null,stat));
                    strategy.addNode(url);
                }


            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

   @Override
   public String getServiceUrl(){
        return strategy.getNode();
   }

    public static void main(String[] args) {
        Executor executor =Executors.newFixedThreadPool(5);
        ZKServiceDiscovery zkServiceDiscovery =new ZKServiceDiscovery();
        zkServiceDiscovery.setStrategy(ZKServiceDiscovery.CONSISTENT_HASH_STRATEGY);
        zkServiceDiscovery.connect();
        zkServiceDiscovery.discover();
        for(int i=0;i<=100;i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(zkServiceDiscovery.getServiceUrl());
                }
            });
        }

    }
}
