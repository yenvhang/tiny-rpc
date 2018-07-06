package top.nvhang.rpc.registry.strategy;

import top.nvhang.rpc.registry.hash.HashAlgorithm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

/**
 * һ����hash �㷨
 * Created by yeyh on 2018/7/5.
 */
public class ConsistentHashStrategy implements LoadBalancingStrategy {
    TreeMap<Long,String> treeMap =new TreeMap<>();
    /**
     * �����ڵ������ڵ�
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
            //�����һ���ڵ㻹Ҫ�����ȡ ��һ���ڵ�
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
