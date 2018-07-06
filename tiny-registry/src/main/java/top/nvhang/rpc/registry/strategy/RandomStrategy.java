package top.nvhang.rpc.registry.strategy;

import java.util.List;
import java.util.Random;

/**
 * Created by yeyh on 2018/7/5.
 */
public class RandomStrategy implements LoadBalancingStrategy {
    Random random=new Random();
    /**
     * ·þÎñµØÖ·²Ö¿â
     */
    private List<String> serviceAddressRepository;
    @Override
    public void addNode(String date) {
        serviceAddressRepository.add(date);
    }

    @Override
    public String getNode() {
        return serviceAddressRepository.get(random.nextInt(serviceAddressRepository.size()));
    }

    @Override
    public void clear() {
        serviceAddressRepository.clear();
    }

}
