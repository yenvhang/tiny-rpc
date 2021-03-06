package top.nvhang.rpc.server;

import java.io.IOException;

/**
 * Created by yeyh on 2018/6/12.
 */
public interface Server {
    /**
     * 服务关闭
     */
    void stop();

    /**
     * 服务启动
     */
    void start() throws IOException;

    /**
     * 注册服务
     * @param inf
     * @param obj
     */
    void registry(Class inf, Object obj) throws ClassNotFoundException;
}
