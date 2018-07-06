package top.nvhang.rpc.registry.strategy;

/**
 * Created by yeyh on 2018/7/5.
 */
public interface LoadBalancingStrategy {
     void addNode(String date);
     String getNode();

     void clear();
}
