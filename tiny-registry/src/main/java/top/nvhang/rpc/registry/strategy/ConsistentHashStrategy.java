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
    /**
     * 单个节点的虚拟节点
     */
    private final int VIRTUAL_NODES =10;
    @Override
    public void addNode(String data){
        for(int i=0;i<VIRTUAL_NODES;i++){
            Long hash = HashAlgorithm.FNV1A_64_HASH.hash(data+"&&VN"+i);
            System.out.println("hash : "+hash+" data : "+data);
            treeMap.put(hash,data);
        }

    }

    @Override
    public String getNode() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();

            Map.Entry<Long,String> node =treeMap.higherEntry(HashAlgorithm.FNV1A_64_HASH.hash(inetAddress.getHostAddress()));
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
