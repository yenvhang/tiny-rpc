package top.nvhang.rpc.registry.strategy;

import top.nvhang.rpc.registry.hash.HashAlgorithm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash 算法
 * Created by yeyh on 2018/7/5.
 */
public class ConsistentHashStrategy implements LoadBalancingStrategy {
    TreeMap<Long,String> treeMap =new TreeMap<>();
    @Override
    public void addNode(String data){
        treeMap.put(HashAlgorithm.FNV1_32_HASH.hash(data),data);
    }

    @Override
    public String getNode() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String address=new String(inetAddress.getAddress());
            Map.Entry<Long,String> node =treeMap.higherEntry(HashAlgorithm.FNV1_32_HASH.hash(address));
            //比最后一个节点还要大，则获取 第一个节点
            if(node==null){
                return treeMap.firstEntry().getValue();
            }
            return node.getValue();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return treeMap.firstEntry().getValue();

    }

    @Override
    public void clear(){
        treeMap.clear();
    }

}
