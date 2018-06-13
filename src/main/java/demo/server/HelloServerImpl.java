package demo.server;

/**
 * Created by yeyh on 2018/6/12.
 */
public class HelloServerImpl implements HelloServer {
    @Override
    public String sayHello(String world) {
        System.out.println("hello~ " +world );
        return world;
    }
}
