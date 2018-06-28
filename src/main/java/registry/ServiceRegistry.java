package registry;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.List;
/**
 * Created by yeyh on 2018/6/27.
 */
public class ServiceRegistry {


    private CountDownLatch countDownLatch =new CountDownLatch(1);

    private static final String RPC_NODE_PATH="/zk_for_rpc";

   public void register(String data){
       if(data!=null){
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
                    if(Event.KeeperState.SyncConnected.equals(watchedEvent.getState())){
                        countDownLatch.countDown();
                    }
                }

            });
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }



    private void createNode(ZooKeeper zk,String date){
        try {
            if(zk.exists(RPC_NODE_PATH,false)==null){
                zk.create(RPC_NODE_PATH,date.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
