package top.nvhang.rpc.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yeyh on 2018/6/27.
 */
public class ServiceRegistry {


    private CountDownLatch countDownLatch =new CountDownLatch(1);
    private Logger logger = LoggerFactory.getLogger(getClass());


   public void register(String data){
       if(!StringUtils.isEmpty(data)){
           ZooKeeper zooKeeper =connect();
           if(zooKeeper!=null){
               createNode(zooKeeper,data);
           }
       }
   }

    private ZooKeeper connect(){
        ZooKeeper zooKeeper = null;
        try {
             zooKeeper =new ZooKeeper("121.196.216.17:2181",5000 , new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(Watcher.Event.KeeperState.SyncConnected.equals(watchedEvent.getState())){
                        countDownLatch.countDown();
                    }
                }

            });
            countDownLatch.await();
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage(),e);
        }
        return zooKeeper;
    }



    private void createNode(ZooKeeper zk,String date){
        try {
            if(zk.exists(Constant.RPC_REGISTRY_PATH,false) == null){
                zk.create(Constant.RPC_REGISTRY_PATH,date.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            //如果服务断开连接 则 取消服务的注册
            zk.create(Constant.RPC_NODE_PATH,date.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("service url success regist");
        } catch (KeeperException | InterruptedException e) {
            logger.error(e.getMessage(),e);
        }

    }


}
