package demo.server;

import core.NIOServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ServerMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        new ClassPathXmlApplicationContext("spring-config.xml").start();
    }
}
