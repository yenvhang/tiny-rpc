package core;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yeyh on 2018/6/28.
 */
public class ServiceDiscover {

    private BIOClient bioClient;
    private CountDownLatch countDownLatch =new CountDownLatch(1);
    private static final String RPC_NODE_PATH="/zk_for_rpc";
    public ServiceDiscover(BIOClient bioClient) {
        this.bioClient = bioClient;
    }

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
                byte [] datas =zooKeeper.getData(RPC_NODE_PATH, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if(event.getType()==Event.EventType.NodeDataChanged){

                            discover();
                        }
                    }
                },stat);
                bioClient.updateSocketAdress(new String(datas));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
